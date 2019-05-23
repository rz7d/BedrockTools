package com.github.stilllogic20.bedrocktools.common.network;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.util.text.TextFormatting.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SPacketMiningModeChanged implements IMessage {

    private ItemBedrockPickaxe.MiningMode mode;

    public SPacketMiningModeChanged() {
    }

    public SPacketMiningModeChanged(ItemBedrockPickaxe.MiningMode mode) {
        this.mode = mode;
    }

    public ItemBedrockPickaxe.MiningMode getMode() {
        final ItemBedrockPickaxe.MiningMode mode = this.mode;
        if (mode == null)
            throw new IllegalStateException("Message is not initialized");
        return mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final ItemBedrockPickaxe.MiningMode[] values = ItemBedrockPickaxe.MiningMode.values();
        this.mode = values[Math.min(buf.readInt(), values.length)];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(getMode().ordinal());
    }

    public static final class Handler implements IMessageHandler<SPacketMiningModeChanged, IMessage> {

        @Override
        @Nullable
        public IMessage onMessage(SPacketMiningModeChanged message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT)
                throw new AssertionError();
            final ItemBedrockPickaxe.MiningMode mode = message.getMode();

            final Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                mc.player.sendMessage(new TextComponentString(String.format("%s[%sBedrockTools%s]%s %s: %s%s(%.0f)",
                    DARK_GRAY, GRAY, DARK_GRAY, WHITE,
                    I18n.format("bedrocktools.item.tooltip.miningmode"),
                    BLUE,
                    I18n.format("bedrocktools.mode." + mode.name().toLowerCase()),
                    mode.efficiency())));
            });
            return null;
        }

    }

}
