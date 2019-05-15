package com.github.stilllogic20.bedrocktools.common.network;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockSword;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.util.text.TextFormatting.*;

public class SPacketWeaponModeChanged implements IMessage, IMessageHandler<SPacketWeaponModeChanged, IMessage> {

    @Nullable
    private ItemBedrockSword.WeaponMode mode;

    public SPacketWeaponModeChanged() {
    }

    public SPacketWeaponModeChanged(@Nonnull ItemBedrockSword.WeaponMode mode) {
        this.mode = mode;
    }

    @Nonnull
    public ItemBedrockSword.WeaponMode getMode() {
        final ItemBedrockSword.WeaponMode mode = this.mode;
        if (mode == null)
            throw new IllegalStateException("Message is not initialized");
        return mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final ItemBedrockSword.WeaponMode[] values = ItemBedrockSword.WeaponMode.values();
        this.mode = values[Math.min(buf.readInt(), values.length)];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(getMode().ordinal());
    }

    @Override
    public IMessage onMessage(SPacketWeaponModeChanged message, MessageContext ctx) {
        if (ctx.side != Side.CLIENT)
            throw new AssertionError();
        @Nonnull final ItemBedrockSword.WeaponMode mode = message.getMode();
        final Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            mc.player.sendMessage(new TextComponentString(String.format("%s[%sBedrockTools%s]%s %s: %s%s",
                DARK_GRAY, GRAY, DARK_GRAY, WHITE,
                I18n.format("bedrocktools.item.tooltip.weaponmode"),
                BLUE,
                I18n.format("bedrocktools.mode." + mode.name().toLowerCase()))));
        });
        return null;
    }

}
