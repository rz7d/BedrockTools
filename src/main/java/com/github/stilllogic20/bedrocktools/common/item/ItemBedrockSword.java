package com.github.stilllogic20.bedrocktools.common.item;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.WHITE;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.BedrockToolsMaterial;
import com.github.stilllogic20.bedrocktools.common.util.NBTAccess;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBedrockSword extends ItemSword {

    private static final String NAME = "bedrock_sword";
    private static final String MODE_KEY = "bedrocktools.sword_mode";
    private static final String SUBACTION_MODE_KEY = "subaction";

    public enum WeaponMode {
        CRITICAL_ATTACK,
        RANGED,
        SHIELD,
        NONE;

        @Nonnull
        public WeaponMode next() {
            final WeaponMode[] values = values();
            final WeaponMode next = values[(ordinal() + 1) % values.length];
            Objects.requireNonNull(next);
            return next;
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
    public static WeaponMode getWeaponMode(@Nonnull ItemStack item) {
        return prepare(item).getEnum(SUBACTION_MODE_KEY, WeaponMode.values()).orElse(WeaponMode.SHIELD);
    }

    public static void setWeaponMode(@Nonnull ItemStack item, @Nonnull WeaponMode mode) {
        prepare(item).setEnum(SUBACTION_MODE_KEY, mode);
    }

    public ItemBedrockSword() {
        super(BedrockToolsMaterial.BEDROCK);
        setTranslationKey(NAME);
        setRegistryName(BedrockToolsMod.MODID, NAME);
        this.addPropertyOverride(new ResourceLocation("blocking"),
                (stack, worldIn, entityIn) -> entityIn != null && entityIn.isHandActive()
                        && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
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
    @SuppressWarnings("deprecation")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return super.onItemRightClick(world, player, hand);

        final ItemStack item = player.getHeldItem(hand);
        if (item == null)
            return super.onItemRightClick(world, player, hand);

        final WeaponMode mode = getWeaponMode(item);

        if (player.isSneaking()) {
            WeaponMode newMode = mode.next();
            setWeaponMode(item, newMode);
            player.sendMessage(new TextComponentString(String.format("%s[%sBedrockTools%s]%s %s: %s%s",
                    DARK_GRAY, GRAY, DARK_GRAY, WHITE,
                    net.minecraft.util.text.translation.I18n
                            .translateToLocal("bedrocktools.item.tooltip.weaponmode"),
                    BLUE,
                    net.minecraft.util.text.translation.I18n
                            .translateToLocal("bedrocktools.mode." + newMode.name().toLowerCase()))));
            return new ActionResult<>(EnumActionResult.SUCCESS, item);
        }

        switch (mode) {
        case NONE:
            return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
        case CRITICAL_ATTACK: // TODO: implement critical attack
            // fall through
        case RANGED: // TODO: implement ranged attack
            return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
        case SHIELD: // fall through
        default:
            player.setActiveHand(hand);
            break;
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
    }

}
