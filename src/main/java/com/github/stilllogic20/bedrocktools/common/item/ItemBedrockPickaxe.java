package com.github.stilllogic20.bedrocktools.common.item;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;
import com.github.stilllogic20.bedrocktools.common.util.BlockFinder;
import com.github.stilllogic20.bedrocktools.common.util.NBTAccess;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBedrockPickaxe extends ItemPickaxe {

    private static final String NAME = "bedrock_pickaxe";
    private static final String MODE_KEY = "bedrocktools.pickaxe_mode";
    private static final String MINING_MODE_KEY = "efficiency";
    private static final String VEIN_MODE_KEY = "vein";

    static enum MiningMode {
        NORMAL(20F),
        MIDDLE(12F),
        SLOW(8F),
        FAST(128F),
        INSANE(Float.MAX_VALUE),
        OFF(0F);

        private final float efficiency;

        private MiningMode(float efficiency) {
            this.efficiency = efficiency;
        }

        @Nonnull
        public MiningMode next() {
            final MiningMode[] values = values();
            final MiningMode next = values[(ordinal() + 1) % values.length];
            Objects.requireNonNull(next);
            return next;
        }

        public float efficiency() {
            return efficiency;
        }

    }

    public enum VeinMode {
        NORMAL(3),
        MORE(5),
        ALL(-1),
        OFF(0);

        private final int range;

        private VeinMode(int range) {
            this.range = range;
        }

        @Nonnull
        public VeinMode next() {
            final VeinMode[] values = values();
            final VeinMode next = values[(ordinal() + 1) % values.length];
            Objects.requireNonNull(next);
            return next;
        }

        public int range() {
            return range;
        }

    }

    @Nonnull
    private static NBTAccess prepare(@Nonnull ItemStack item) {
        @Nonnull
        final NBTAccess access = new NBTAccess(item).prepare();
        access.compareAndSet(MODE_KEY, null, new NBTTagCompound());
        return access;
    }

    @Nonnull
    public static MiningMode getMiningMode(@Nonnull ItemStack item) {
        return prepare(item).getEnum(MINING_MODE_KEY, MiningMode.values()).orElse(MiningMode.NORMAL);
    }

    @Nonnull
    public static VeinMode getVeinMode(@Nonnull ItemStack item) {
        return prepare(item).getEnum(VEIN_MODE_KEY, VeinMode.values()).orElse(VeinMode.OFF);
    }

    public static void setMiningMode(@Nonnull ItemStack item, @Nonnull MiningMode miningMode) {
        prepare(item).setEnum(MINING_MODE_KEY, miningMode);
    }

    public static void setVeinMode(@Nonnull ItemStack item, @Nonnull VeinMode veinMode) {
        prepare(item).setEnum(VEIN_MODE_KEY, veinMode);
    }

    public ItemBedrockPickaxe() {
        super(BedrockToolsMaterial.BEDROCK);
        setTranslationKey(NAME);
        setRegistryName(BedrockToolsMod.MODID, NAME);
        setHarvestLevel("pickaxe", -1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (stack != null) {
            MiningMode miningMode = getMiningMode(stack);
            VeinMode veinMode = getVeinMode(stack);
            tooltip.add(String.format("%s: %s%s",
                    I18n.format("bedrocktools.item.tooltip.miningmode"), BLUE,
                    I18n.format("bedrocktools.mode." + miningMode.name().toLowerCase())));
            tooltip.add(String.format("%s: %s%.0f",
                    I18n.format("bedrocktools.item.tooltip.efficiency"), BLUE,
                    miningMode.efficiency()));
            tooltip.add(String.format("%s: %s%s",
                    I18n.format("bedrocktools.item.tooltip.veinmode"), BLUE,
                    I18n.format("bedrocktools.mode." + veinMode.name().toLowerCase())));
        }
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockState) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (stack != null) {
            final MiningMode mode = getMiningMode(stack);
            assert mode != null;
            return mode.efficiency();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean hitEntity(ItemStack item, EntityLivingBase target, EntityLivingBase attacker) {
        final float damage = toolMaterial.getAttackDamage();
        if (attacker instanceof EntityPlayer) {
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage * 0.9F);
            target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage * 0.1F);
        } else {
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage);
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return super.onItemRightClick(world, player, hand);

        final ItemStack item = player.getHeldItem(hand);
        if (item != null && player.isSneaking()) {
            MiningMode mode = getMiningMode(item).next();
            setMiningMode(item, mode);
            player.sendMessage(new TextComponentString(String.format("%s[%sBedrockTools%s] %s: %s%s(%.0f)",
                    DARK_GRAY, GRAY, DARK_GRAY,
                    net.minecraft.util.text.translation.I18n.translateToLocal("bedrocktools.item.tooltip.miningmode"),
                    BLUE,
                    net.minecraft.util.text.translation.I18n
                            .translateToLocal("bedrocktools.mode." + mode.name().toLowerCase()),
                    mode.efficiency)));
            return new ActionResult<>(EnumActionResult.SUCCESS, item);
        }
        return new ActionResult<>(EnumActionResult.PASS, item);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
            float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

        IBlockState state = world.getBlockState(pos);
        if (state != null && !player.isSneaking()) {
            if (Float.compare(state.getBlockHardness(world, pos), -1) == 0) {
                breakBlock(world, pos, player);
            }
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (stack == null)
            return super.onBlockStartBreak(stack, pos, player);
        World world = player.world;
        if (world.isRemote)
            return super.onBlockStartBreak(stack, pos, player);
        IBlockState state = world.getBlockState(pos);
        VeinMode veinMode = getVeinMode(stack);
        Block block = state.getBlock();
        switch (veinMode) {
        case OFF:
            break;
        case ALL:
            Set<BlockPos> found = BlockFinder.of(block, 128, world, pos).find();
            if (found.size() <= (BlockFinder.isOre(block) ? 128 : 9)) {
                found.stream().forEach(p -> breakBlock(world, p, player));
            }
            break;
        case NORMAL:
        case MORE:
            int range = (veinMode.range() - 1) / 2;
            CompletableFuture.runAsync(() -> {
                // Generate BlockPos(int, int, int) from three IntStreams
                IntStream.rangeClosed(-range, range)
                        .mapToObj(x -> IntStream.rangeClosed(-range, range)
                                .mapToObj(z -> IntStream.rangeClosed(-range, range)
                                        .mapToObj(y -> pos.add(x, y, z))))
                        .parallel()
                        .flatMap(UnaryOperator.identity())
                        .flatMap(UnaryOperator.identity())
                        .filter(b -> world.getBlockState(b).getBlock() == block)
                        .sorted(Comparator.comparing(b -> pos.distanceSq(b)))
                        .limit(BlockFinder.isOre(block) ? 128 : 9)
                        .forEach(b -> breakBlock(world, b, player));
            });
            break;
        default:
            break;
        }
        return super.onBlockStartBreak(stack, pos, player);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        if (world.isRemote)
            return super.canDestroyBlockInCreative(world, pos, stack, player);
        if (stack != null)
            return getMiningMode(stack) != MiningMode.OFF;
        return super.canDestroyBlockInCreative(world, pos, stack, player);
    }

    private static void breakBlock(World world, BlockPos position, EntityPlayer player) {
        IBlockState state = world.getBlockState(position);
        Block block = state.getBlock();
        block.onBlockHarvested(world, position, state, player);
        world.playEvent(null, 2001, position, Block.getStateId(state));
        world.setBlockToAir(position);
        block.breakBlock(world, position, state);
        MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, position, state, player));

        if (block == Blocks.LIT_REDSTONE_ORE)
            block = Blocks.REDSTONE_ORE;

        ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
        if (stack.isEmpty()) {
            block.dropBlockAsItem(world, position, state, 0);
        } else {
            world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stack));
        }
    }

}
