package com.github.stilllogic20.bedrocktools.common.item;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;
import com.github.stilllogic20.bedrocktools.common.init.Messages;
import com.github.stilllogic20.bedrocktools.common.network.SPacketWeaponModeChanged;
import com.github.stilllogic20.bedrocktools.common.util.NBTAccess;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.text.TextFormatting.BLUE;

public class ItemBedrockSword extends ItemSword {

    private static final String NAME = "bedrock_sword";
    private static final String MODE_KEY = "bedrocktools.sword_mode";
    private static final String SUBACTION_MODE_KEY = "subaction";

    public ItemBedrockSword() {
        super(BedrockToolsMaterial.BEDROCK);
        setTranslationKey(NAME);
        setRegistryName(BedrockToolsMod.MODID, NAME);
        this.addPropertyOverride(new ResourceLocation("blocking"),
            (stack, worldIn, entityIn) -> entityIn != null && entityIn.isHandActive()
                && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
    }

    @Nonnull
    private static NBTAccess prepare(@Nonnull ItemStack item) {
        @Nonnull final NBTAccess access = new NBTAccess(item).prepare();
        access.compareAndSet(MODE_KEY, null, new NBTTagCompound());
        return access;
    }

    @Nonnull
    public static WeaponMode getWeaponMode(@Nonnull ItemStack item) {
        return prepare(item).getEnum(SUBACTION_MODE_KEY, WeaponMode.values()).orElse(WeaponMode.SHIELD);
    }

    public static void setWeaponMode(@Nonnull ItemStack item, @Nonnull WeaponMode mode) {
        prepare(item).setEnum(SUBACTION_MODE_KEY, mode);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (stack != null) {
            WeaponMode mode = getWeaponMode(stack);
            tooltip.add(String.format("%s: %s%s",
                I18n.format("bedrocktools.item.tooltip.weaponmode"), BLUE,
                I18n.format("bedrocktools.mode." + mode.name().toLowerCase())));
        }
    }

    @Override
    public boolean hitEntity(ItemStack item, EntityLivingBase target, EntityLivingBase attacker) {
        final float damage = getAttackDamage();
        if (attacker instanceof EntityPlayer) {
            if (!(target instanceof EntityPlayer)) {
                target.setHealth(target.getHealth() * 0.09F);
            }
            target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), damage * 0.75F);
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage * 0.75F);
        } else {
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage);
        }
        return false;
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
    public boolean isShield(ItemStack stack, EntityLivingBase entity) {
        return true;
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
    public boolean canHarvestBlock(IBlockState blockIn) {
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
                                    EntityLivingBase entityLiving) {
        return false;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if (stack != null) {
            WeaponMode mode = getWeaponMode(stack);
            switch (mode) {
                case NONE:
                    return EnumAction.NONE;
                case CRITICAL_ATTACK:
                    // TODO: Implement
                    return EnumAction.EAT;
                case RANGED:
                    // TODO: Implement
                    return EnumAction.DRINK;
                case SHIELD: // fall through
                default:
                    return EnumAction.BLOCK;
            }
        }
        return super.getItemUseAction(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        if (stack != null) {
            WeaponMode mode = getWeaponMode(stack);
            switch (mode) {
                case NONE:
                case HOE:
                    return 0;
                case CRITICAL_ATTACK: // fall-through
                case RANGED:
                    return 32;
                case SHIELD: // fall-through
                default:
                    return 72000;
            }
        }
        return super.getMaxItemUseDuration(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return super.onItemRightClick(world, player, hand);

        final ItemStack item = player.getHeldItem(hand);
        final WeaponMode mode = getWeaponMode(item);

        if (player.isSneaking()) {
            WeaponMode newMode = mode.next();
            setWeaponMode(item, newMode);
            Messages.NETWORK.sendTo(new SPacketWeaponModeChanged(newMode), (EntityPlayerMP) player);
            return new ActionResult<>(EnumActionResult.SUCCESS, item);
        }

        switch (mode) {
            case NONE: // fall through
            case CRITICAL_ATTACK: // fall through
            case RANGED:
                return new ActionResult<>(EnumActionResult.PASS, item);
            case SHIELD: // fall through
            default:
                player.setActiveHand(hand);
                break;
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, item);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        switch (getWeaponMode(stack)) {
            case HOE:
                return useHoe(stack, player, worldIn, pos, facing);
            default:
                return EnumActionResult.PASS;
        }
    }

    private EnumActionResult useHoe(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumFacing facing) {
        if (!player.canPlayerEdit(pos.offset(facing), facing, stack))
            return EnumActionResult.FAIL;

        int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(stack, player, worldIn, pos);
        if (hook != 0) return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;

        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (facing == EnumFacing.DOWN || !worldIn.isAirBlock(pos.up()))
            return EnumActionResult.PASS;

        if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
            setBlock(stack, player, worldIn, pos, Blocks.FARMLAND.getDefaultState());
            return EnumActionResult.SUCCESS;
        }

        if (block == Blocks.DIRT) {
            switch (state.getValue(BlockDirt.VARIANT)) {
                case DIRT:
                    setBlock(stack, player, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                    return EnumActionResult.SUCCESS;
                case COARSE_DIRT:
                    setBlock(stack, player, worldIn, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                    return EnumActionResult.SUCCESS;
                case PODZOL: // fall-through
                default:
                    break;
            }
        }

        return EnumActionResult.PASS;
    }

    private void setBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state) {
        world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!world.isRemote) {
            world.setBlockState(pos, state, 11);
            stack.damageItem(1, player);
        }
    }

    @Override
    @Nonnull
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) getAttackDamage() + 3.0F, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", Integer.MAX_VALUE, 0));
        }

        return multimap;
    }

    public enum WeaponMode {
        CRITICAL_ATTACK,
        RANGED,
        SHIELD,
        HOE,
        NONE;

        @Nonnull
        public WeaponMode next() {
            final WeaponMode[] values = values();
            final WeaponMode next = values[(ordinal() + 1) % values.length];
            Objects.requireNonNull(next);
            return next;
        }

    }

}
