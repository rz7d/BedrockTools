package com.github.stilllogic20.bedrocktools.common.item;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;
import com.github.stilllogic20.bedrocktools.common.init.Messages;
import com.github.stilllogic20.bedrocktools.common.network.MiningModeChangedMessage;
import com.github.stilllogic20.bedrocktools.common.util.BlockFinder;
import com.github.stilllogic20.bedrocktools.common.util.NBTAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static net.minecraft.util.text.TextFormatting.BLUE;

public class ItemBedrockPickaxe extends ItemPickaxe {

    private static final String NAME = "bedrock_pickaxe";
    private static final String MODE_KEY = "bedrocktools.pickaxe_mode";
    private static final String MINING_MODE_KEY = "efficiency";
    private static final String VEIN_MODE_KEY = "vein";

    public ItemBedrockPickaxe() {
        super(BedrockToolsMaterial.BEDROCK);
        setTranslationKey(NAME);
        setRegistryName(BedrockToolsMod.MODID, NAME);
        setHarvestLevel("pickaxe", -1);
    }

    @Nonnull
    private static NBTAccess prepare(@Nonnull ItemStack item) {
        @Nonnull final NBTAccess access = new NBTAccess(item).prepare();
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

    private static void breakBlock(ItemStack pickaxe, World world, BlockPos position, EntityPlayer player, boolean force) {
        if (world.isRemote)
            return;

        int _fortune = 0;
        int _silktouch = 0;

        if (!force) {
            for (NBTBase nbt : pickaxe.getEnchantmentTagList()) {
                if (nbt instanceof NBTTagCompound) {
                    NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                    int id = tagCompound.getShort("id");
                    int level = tagCompound.getShort("lvl");

                    Enchantment enchantment = Enchantment.getEnchantmentByID(id);
                    if (enchantment == null) {
                        continue;
                    }

                    if (Objects.equals(enchantment, Enchantments.FORTUNE)) {
                        _fortune = level;
                    }

                    if (Objects.equals(enchantment, Enchantments.SILK_TOUCH)) {
                        _silktouch = level;
                    }
                }
            }
        }

        final int fortune = _fortune;
        final int silktouch = _silktouch;

        final MinecraftServer server = world.getMinecraftServer();
        if (server == null) {
            throw new IllegalStateException("MinecraftServer is not initialized");
        }

        IBlockState state = world.getBlockState(position);
        Block block = state.getBlock();
        server.addScheduledTask(() -> {
            block.onBlockHarvested(world, position, state, player);
            world.setBlockToAir(position);
            block.breakBlock(world, position, state);
            MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, position, state, player));
        });

        final int x = position.getX();
        final int y = position.getY();
        final int z = position.getZ();

        server.addScheduledTask(() -> {
            if (force) {
                dropItemStack(new ItemStack(block, 1, block.getMetaFromState(state)), world, x, y, z);
                return;
            }

            if (silktouch > 0 && block.canSilkHarvest(world, position, state, player)) {
                dropItemStack(block.getSilkTouchDrop(state), world, x, y, z);
                return;
            }

            final NonNullList<ItemStack> drops = NonNullList.create();
            block.getDrops(drops, world, position, state, fortune);
            drops.forEach(i -> dropItemStack(i, world, x, y, z));
        });
    }

    private static void dropItemStack(@Nonnull ItemStack stack, @Nonnull World world, double x, double y, double z) {
        final EntityItem entity = new EntityItem(world, x, y, z, stack);
        entity.setNoPickupDelay();
        world.spawnEntity(entity);
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
        return getMiningMode(stack).efficiency();
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return super.onItemRightClick(world, player, hand);

        final ItemStack item = player.getHeldItem(hand);
        if (player.isSneaking()) {
            MiningMode mode = getMiningMode(item).next();
            setMiningMode(item, mode);
            Messages.S_NETWORK.sendTo(new MiningModeChangedMessage(mode), (EntityPlayerMP) player);
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
        if (!player.isSneaking()) {
            if (Float.compare(state.getBlockHardness(world, pos), -1) == 0) {
                breakBlock(player.getHeldItemMainhand(), world, pos, player, true);
            }
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (stack == null || pos == null || player == null)
            return super.onBlockStartBreak(stack, pos, player);
        World world = player.world;
        if (world.isRemote)
            return super.onBlockStartBreak(stack, pos, player);

        IBlockState state = world.getBlockState(pos);
        VeinMode veinMode = getVeinMode(stack);
        Block block = state.getBlock();
        final MinecraftServer server = world.getMinecraftServer();
        Objects.requireNonNull(server);
        switch (veinMode) {
            case ALL:
                BlockFinder.of(block, 127, world, pos).find().thenAcceptAsync(found -> {
                    if (found.size() <= (BlockFinder.isOre(block) ? 127 : 27)) {
                        found.forEach(p -> breakBlock(stack, world, p, player, false));
                        found.stream().filter(p -> p != pos).forEach(p ->
                            world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, p, Block.getStateId(world.getBlockState(p))));
                    } else {
                        breakBlock(stack, world, pos, player, false);
                    }
                });
                return true;
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
                        .sorted(Comparator.comparing(pos::distanceSq))
                        .limit(BlockFinder.isOre(block) ? 127 : 27)
                        .forEach(b -> breakBlock(stack, world, b, player, false));
                });
                return true;
            case OFF: // fall-through
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
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        if (world.isRemote)
            return super.canDestroyBlockInCreative(world, pos, stack, player);
        if (stack != null) {
            return getMiningMode(stack) != MiningMode.OFF;
        }
        return super.canDestroyBlockInCreative(world, pos, stack, player);
    }

    public enum MiningMode {
        NORMAL(20F),
        MIDDLE(12F),
        SLOW(8F),
        FAST(128F),
        INSANE(Float.MAX_VALUE),
        OFF(0F);

        private final float efficiency;

        MiningMode(float efficiency) {
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

        VeinMode(int range) {
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

}
