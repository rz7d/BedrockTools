package com.github.stilllogic20.bedrocktools.common.network;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.VeinMode;
import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CPacketVeinModeChanged implements IMessage {

    private VeinMode mode;

    public CPacketVeinModeChanged() {
    }

    public CPacketVeinModeChanged(VeinMode mode) {
        this.mode = mode;
    }

    public VeinMode getMode() {
        final VeinMode mode = this.mode;
        if (mode == null)
            throw new IllegalStateException("Message is not initialized");
        return mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final VeinMode[] values = VeinMode.values();
        this.mode = values[Math.min(buf.readInt(), values.length)];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(getMode().ordinal());
    }

    public static final class Handler implements IMessageHandler<CPacketVeinModeChanged, IMessage> {

        @Override
        @Nullable
        public IMessage onMessage(CPacketVeinModeChanged message, MessageContext ctx) {
            if (ctx.side != Side.SERVER)
                throw new AssertionError();
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack stack = player.getHeldItemMainhand();
            if (message != null && stack.getItem() instanceof ItemBedrockPickaxe) {
                ItemBedrockPickaxe.setVeinMode(stack, message.getMode());
            }
            return null;
        }

    }

}
