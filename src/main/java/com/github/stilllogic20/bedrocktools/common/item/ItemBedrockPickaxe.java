package com.github.stilllogic20.bedrocktools.common.item;

import java.util.List;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBedrockPickaxe extends ItemPickaxe {

    private static final float ATTACK_DAMAGE = 22F;
    private static final String NAME = "bedrock_pickaxe";
    private static final String MODE_KEY = "bedrocktools.pickaxe_mode";

    static enum EfficiencyMode {
        NORMAL(20F),
        MIDDLE(12F),
        SLOW(8F),
        FAST(128F),
        INSANE(Float.MAX_VALUE),
        OFF(0F);

        private final float efficiency;

        private EfficiencyMode(float efficiency) {
            this.efficiency = efficiency;
        }

        public EfficiencyMode next() {
            final EfficiencyMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

    }

    public enum VeinMode {
        NORMAL(10F),
        MORE(20F),
        INSANE(Float.MAX_VALUE),
        OFF(0F);

        private final float range;

        private VeinMode(float range) {
            this.range = range;
      }

      public VeinMode next() {
        final VeinMode[] values = values();
        return values[(ordinal() + 1) % values.length];
      }

      public float getRange(){
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

    public EfficiencyMode getEfficiencyMode(ItemStack item) {
        return hasTag(item) ? EfficiencyMode.values()[getTag(item).getInteger("efficiency")] : EfficiencyMode.NORMAL;
    }

    public VeinMode getVeinMode(ItemStack item) {
        return hasTag(item) ? VeinMode.values()[getTag(item).getInteger("vein")] : VeinMode.NORMAL;
    }

    public void setMode(ItemStack item, EfficiencyMode efficiencyMode, VeinMode veinMode) {
        if (item.getTagCompound() == null)
            item.setTagCompound(new NBTTagCompound());
        if (!item.getTagCompound().hasKey(MODE_KEY))
            item.getTagCompound().setTag(MODE_KEY, new NBTTagCompound());
        if (!item.getTagCompound().hasKey("efficiency") && efficiencyMode != null)
            getTag(item).setInteger("efficiency", efficiencyMode.ordinal());
        if (!item.getTagCompound().hasKey("vein") && veinMode != null)
            getTag(item).setInteger("vein", veinMode.ordinal());
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

        EfficiencyMode efficiencyMode = getEfficiencyMode(stack);
        VeinMode veinMode = getVeinMode(stack);
        tooltip.add(
                String.format("%s: %s%s",
                        net.minecraft.client.resources.I18n.format("bedrocktools.item.tooltip.miningmode"),
                        TextFormatting.BLUE,
                        net.minecraft.client.resources.I18n.format("bedrocktools.mode." + efficiencyMode.name().toLowerCase())));
        tooltip.add(
                String.format("%s: %s%.0f",
                        net.minecraft.client.resources.I18n.format("bedrocktools.item.tooltip.efficiency"),
                        TextFormatting.BLUE,
                        efficiencyMode.efficiency));
        tooltip.add(
               String.format("%s: %s%s",
                        net.minecraft.client.resources.I18n.format("bedrocktools.item.tooltip.veinmode"),
                        TextFormatting.BLUE,
                        net.minecraft.client.resources.I18n.format("bedrocktools.mode." + veinMode.name().toLowerCase())));

    }

    @Override
    public boolean canHarvestBlock(IBlockState blockState) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack item, IBlockState blockState) {
        final EfficiencyMode mode = this.getEfficiencyMode(item);
        assert mode != null;
        return mode.efficiency;
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
            EfficiencyMode mode = getEfficiencyMode(item).next();
            setMode(item, mode, null);

            player.sendMessage(new TextComponentString(
                    String.format("[BedrockTools] %s: %s%s(%.0f)",
                            net.minecraft.util.text.translation.I18n
                                    .translateToLocal("bedrocktools.item.tooltip.miningmode"),
                            TextFormatting.BLUE,
                            net.minecraft.util.text.translation.I18n
                                    .translateToLocal("bedrocktools.mode." + mode.name().toLowerCase()),
                            mode.efficiency)));
            return new ActionResult<>(EnumActionResult.SUCCESS, item);
        }
        return new ActionResult<>(EnumActionResult.PASS, item);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ) {
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
        return getEfficiencyMode(stack) != EfficiencyMode.OFF;
    }

}
