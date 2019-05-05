package com.github.stilllogic20.bedrocktools.common.item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;
import com.github.stilllogic20.bedrocktools.common.util.BlockFinder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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

    private static final float ATTACK_DAMAGE = 22F;
    private static final String NAME = "bedrock_pickaxe";
    private static final String MODE_KEY = "bedrocktools.pickaxe_mode";
    private static final String MINING_MODE_KEY = "efficiency";
    private static final String VEIN_MODE_KEY = "vein";

    static enum MiningMode {
        NORMAL(20F), MIDDLE(12F), SLOW(8F), FAST(128F), INSANE(Float.MAX_VALUE), OFF(0F);

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
        NORMAL(3), MORE(5), ALL(-1), OFF(0);

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

    private static boolean hasTag(ItemStack item) {
        final NBTTagCompound tagCompound = item.getTagCompound();
        return tagCompound != null && tagCompound.hasKey(MODE_KEY);
    }

    private static NBTTagCompound getTag(ItemStack item) {
        return item.getTagCompound().getCompoundTag(MODE_KEY);
    }

    private static void initTags(ItemStack item) {
        if (item.getTagCompound() == null)
            item.setTagCompound(new NBTTagCompound());
        if (!item.getTagCompound().hasKey(MODE_KEY))
            item.getTagCompound().setTag(MODE_KEY, new NBTTagCompound());
    }

    public MiningMode getMiningMode(@Nonnull ItemStack item) {
        return hasTag(item) ? MiningMode.values()[getTag(item).getInteger(MINING_MODE_KEY)] : MiningMode.NORMAL;
    }

    public VeinMode getVeinMode(@Nonnull ItemStack item) {
        int index = getTag(item).getInteger(VEIN_MODE_KEY);
        VeinMode[] values = VeinMode.values();
        if (index >= values.length)
            index = 0;
        return hasTag(item) ? values[index] : VeinMode.NORMAL;
    }

    public void setMiningMode(@Nonnull ItemStack item, @Nonnull MiningMode miningMode) {
        initTags(item);
        if (!item.getTagCompound().hasKey(MINING_MODE_KEY))
            getTag(item).setInteger(MINING_MODE_KEY, miningMode.ordinal());
    }

    public void setVeinMode(@Nonnull ItemStack item, @Nonnull VeinMode veinMode) {
        initTags(item);
        if (!item.getTagCompound().hasKey(VEIN_MODE_KEY))
            getTag(item).setInteger(VEIN_MODE_KEY, veinMode.ordinal());
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
        if (attacker instanceof EntityPlayer) {
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, ATTACK_DAMAGE * 0.9F);
            target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), ATTACK_DAMAGE * 0.1F);
        } else {
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), ATTACK_DAMAGE);
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
            final Block block = state.getBlock();
            if (Float.compare(state.getBlockHardness(world, pos), -1) == 0) {
                block.onBlockHarvested(world, pos, state, player);
                world.setBlockToAir(pos);
                world.playEvent(null, 2001, pos, Block.getStateId(state));
                block.breakBlock(world, pos, state);
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(block)));
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
                found.stream().filter(p -> !Objects.equals(p, pos)).forEach(p -> breakBlock(world, p, player));
            }
            break;
        case NORMAL:
        case MORE:
            int range = (veinMode.range() - 1) / 2;
            EnumFacing facing = player.getHorizontalFacing();
            boolean isEW = facing == EnumFacing.EAST | facing == EnumFacing.WEST;

            IntStream.rangeClosed(-range, range).mapToObj(x -> IntStream.rangeClosed(-range, range).mapToObj(
                    z -> IntStream.rangeClosed(-range, range).mapToObj(y -> pos.add(isEW ? x : z, y, isEW ? z : x)))) // Generate
                                                                                                                      // BlockPos(int,
                                                                                                                      // int,
                                                                                                                      // int)
                                                                                                                      // from
                                                                                                                      // three
                                                                                                                      // IntStreams
                    .flatMap(UnaryOperator.identity()).flatMap(UnaryOperator.identity()) // please gimme flatMapToObj
                    .filter(b -> world.getBlockState(b).getBlock() == block) // filter block kind
                    .sorted(Comparator.comparing(b -> pos.distanceSq(b))).limit(isOre(block) ? 128 : 9)
                    .forEach(b -> breakBlock(world, b, player));
            break;
        default:
            break;
        }
        return super.onBlockStartBreak(stack, pos, player);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos,
            EntityLivingBase entity) {
        return super.onBlockDestroyed(stack, world, state, pos, entity);
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
        block.dropBlockAsItem(world, position, state, 0);
        MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, position, state, player));
    }

    private static boolean isOre(Block block) {
        return Arrays.stream(OreDictionary.getOreIDs(new ItemStack(block))).mapToObj(OreDictionary::getOreName)
                .anyMatch(name -> name.startsWith("ore") || name.equals("logWood") || name.equals("treeLeaves"));
    }

}
