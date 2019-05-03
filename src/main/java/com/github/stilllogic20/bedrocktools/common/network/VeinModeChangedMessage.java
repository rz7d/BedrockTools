package com.github.stilllogic20.bedrocktools.common.network;

import com.github.stilllogic20.bedrocktools.common.init.Items;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.VeinMode;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class VeinModeChangedMessage implements IMessage, IMessageHandler<VeinModeChangedMessage, IMessage> {

    private VeinMode mode;

    public VeinModeChangedMessage() {
    }

    public VeinModeChangedMessage(VeinMode mode) {
        this.mode = mode;
    }

    public VeinMode getMode() {
        return mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mode = VeinMode.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mode.ordinal());
    }

    @Override
    public IMessage onMessage(VeinModeChangedMessage message, MessageContext ctx) {
        assert ctx.side == Side.SERVER;
        EntityPlayer player = ctx.getServerHandler().player;
        ItemStack itemInMainHand = player.getHeldItemMainhand();
        if (itemInMainHand.getItem() instanceof ItemBedrockPickaxe) {
            Items.BEDROCK_PICKAXE.setVeinMode(itemInMainHand, message.mode);
        }
        return null;
    }

}
