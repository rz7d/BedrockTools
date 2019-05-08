package com.github.stilllogic20.bedrocktools.common.network;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.util.text.TextFormatting.*;

public class MiningModeChangedMessage implements IMessage, IMessageHandler<MiningModeChangedMessage, IMessage> {

    @Nullable
    private ItemBedrockPickaxe.MiningMode mode;

    public MiningModeChangedMessage() {
    }

    public MiningModeChangedMessage(@Nonnull ItemBedrockPickaxe.MiningMode mode) {
        this.mode = mode;
    }

    @Nonnull
    public ItemBedrockPickaxe.MiningMode getMode() {
        final ItemBedrockPickaxe.MiningMode mode = this.mode;
        if (mode == null)
            throw new IllegalStateException("Message is not initialized");
        return mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Objects.requireNonNull(buf);
        final ItemBedrockPickaxe.MiningMode[] values = ItemBedrockPickaxe.MiningMode.values();
        final ItemBedrockPickaxe.MiningMode mode = values[MathHelper.clamp(buf.readInt(), 0, values.length)];
        this.mode = Objects.requireNonNull(mode);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Objects.requireNonNull(buf);
        buf.writeInt(getMode().ordinal());
    }

    @Override
    public IMessage onMessage(MiningModeChangedMessage message, MessageContext ctx) {
        assert ctx.side == Side.CLIENT;
        @Nonnull final ItemBedrockPickaxe.MiningMode mode = message.getMode();
        Minecraft.getMinecraft().player
            .sendMessage(new TextComponentString(String.format("%s[%sBedrockTools%s]%s %s: %s%s(%.0f)",
                DARK_GRAY, GRAY, DARK_GRAY, WHITE,
                I18n.format("bedrocktools.item.tooltip.miningmode"),
                BLUE,
                I18n.format("bedrocktools.mode." + mode.name().toLowerCase()),
                mode.efficiency())));
        return null;
    }

}
