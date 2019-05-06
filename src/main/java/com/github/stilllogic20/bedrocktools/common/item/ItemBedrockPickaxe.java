package com.github.stilllogic20.bedrocktools.common.item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;
import com.github.stilllogic20.bedrocktools.common.util.BlockFinder;
import com.github.stilllogic20.bedrocktools.common.util.NBTAccess;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

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

        public MiningMode next() {
            final MiningMode[] values = values();
            return values[(ordinal() + 1) % values.length];
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

        public VeinMode next() {
            final VeinMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public int range() {
            return range;
        }

    }

    private static NBTAccess prepare(ItemStack item) {
        final NBTAccess access = new NBTAccess(item).prepare();
        access.compareAndSet(MODE_KEY, null, new NBTTagCompound());
        return access;
    }

    public MiningMode getMiningMode(@Nonnull ItemStack item) {
        return prepare(item).getEnum(MINING_MODE_KEY, MiningMode.values()).orElse(MiningMode.NORMAL);
    }

    public VeinMode getVeinMode(@Nonnull ItemStack item) {
        return prepare(item).getEnum(VEIN_MODE_KEY, VeinMode.values()).orElse(VeinMode.OFF);
    }

    public void setMiningMode(@Nonnull ItemStack item, @Nonnull MiningMode miningMode) {
        prepare(item).setEnum(MINING_MODE_KEY, miningMode);
    }

    public void setVeinMode(@Nonnull ItemStack item, @Nonnull VeinMode veinMode) {
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
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        MiningMode miningMode = getMiningMode(stack);
        VeinMode veinMode = getVeinMode(stack);
        tooltip.add(String.format("%s: %s%s",
                net.minecraft.client.resources.I18n.format("bedrocktools.item.tooltip.miningmode"), TextFormatting.BLUE,
                net.minecraft.client.resources.I18n.format("bedrocktools.mode." + miningMode.name().toLowerCase())));
        tooltip.add(String.format("%s: %s%.0f",
                net.minecraft.client.resources.I18n.format("bedrocktools.item.tooltip.efficiency"), TextFormatting.BLUE,
                miningMode.efficiency()));
        tooltip.add(String.format("%s: %s%s",
                net.minecraft.client.resources.I18n.format("bedrocktools.item.tooltip.veinmode"), TextFormatting.BLUE,
                net.minecraft.client.resources.I18n.format("bedrocktools.mode." + veinMode.name().toLowerCase())));

    }

    @Override
    public boolean canHarvestBlock(IBlockState blockState) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack item, IBlockState blockState) {
        final MiningMode mode = this.getMiningMode(item);
        assert mode != null;
        return mode.efficiency();
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

        ItemStack item = player.getHeldItem(hand);
        if (player.isSneaking()) {
            MiningMode mode = getMiningMode(item).next();
            setMiningMode(item, mode);

            player.sendMessage(new TextComponentString(String.format("[BedrockTools] %s: %s%s(%.0f)",
                    net.minecraft.util.text.translation.I18n.translateToLocal("bedrocktools.item.tooltip.miningmode"),
                    TextFormatting.BLUE, net.minecraft.util.text.translation.I18n
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
            if (found.size() <= (isOre(block) ? 128 : 9)) {
                found.stream().forEach(p -> breakBlock(world, p, player));
            }
            break;
        case NORMAL:
        case MORE:
            int range = (veinMode.range() - 1) / 2;
            EnumFacing facing = player.getHorizontalFacing();
            boolean isEW = facing == EnumFacing.EAST | facing == EnumFacing.WEST;

            CompletableFuture.runAsync(() -> {
                // Generate BlockPos(int, int, int) from three IntStreams
                IntStream.rangeClosed(-range, range)
                        .mapToObj(x -> IntStream.rangeClosed(-range, range)
                                .mapToObj(z -> IntStream.rangeClosed(-range, range)
                                        .mapToObj(y -> pos.add(isEW ? x : z, y, isEW ? z : x))))
                        // please gimme flatMapToObj
                        .flatMap(UnaryOperator.identity())
                        .flatMap(UnaryOperator.identity())
                        // filter block type
                        .filter(b -> world.getBlockState(b).getBlock() == block)
                        .sorted(Comparator.comparing(b -> pos.distanceSq(b)))
                        .limit(isOre(block) ? 128 : 9)
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
        return getMiningMode(stack) != MiningMode.OFF;
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
        // [Mekanism] Issues related BoundingBlock
        // block.dropBlockAsItem(world, position, state, 0);

        ItemStack stack = new ItemStack(block);
        if (stack.isEmpty()) {
            block.dropBlockAsItem(world, position, state, 0);
        } else {
            world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stack));
        }
    }

    private static boolean isOre(Block block) {
        if (block == Blocks.LIT_REDSTONE_ORE)
            return true;
        ItemStack itemStack = new ItemStack(block);
        if (itemStack.isEmpty())
            return false;
        return Arrays.stream(OreDictionary.getOreIDs(itemStack)).mapToObj(OreDictionary::getOreName)
                .anyMatch(name -> name.startsWith("ore") || name.equals("logWood") || name.equals("treeLeaves"));
    }

}
