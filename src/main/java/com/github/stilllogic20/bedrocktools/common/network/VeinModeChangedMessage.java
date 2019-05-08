package com.github.stilllogic20.bedrocktools.common.network;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.VeinMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class VeinModeChangedMessage implements IMessage, IMessageHandler<VeinModeChangedMessage, IMessage> {

    @Nullable
    private VeinMode mode;

    public VeinModeChangedMessage() {
    }

    public VeinModeChangedMessage(@Nonnull VeinMode mode) {
        this.mode = mode;
    }

    @Nonnull
    public VeinMode getMode() {
        final VeinMode mode = this.mode;
        if (mode == null)
            throw new IllegalStateException("Message is not initialized");
        return mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Objects.requireNonNull(buf);
        final VeinMode[] values = VeinMode.values();
        final VeinMode mode = values[MathHelper.clamp(buf.readInt(), 0, values.length)];
        this.mode = Objects.requireNonNull(mode);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Objects.requireNonNull(buf);
        buf.writeInt(getMode().ordinal());
    }

    @Override
    public IMessage onMessage(VeinModeChangedMessage message, MessageContext ctx) {
        assert ctx.side == Side.SERVER;
        EntityPlayer player = ctx.getServerHandler().player;
        ItemStack stack = player.getHeldItemMainhand();
        if (message != null && stack.getItem() instanceof ItemBedrockPickaxe) {
            ItemBedrockPickaxe.setVeinMode(stack, message.getMode());
        }
        return null;
    }

}
