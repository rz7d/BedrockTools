package com.github.stilllogic20.bedrocktools.client.init;

import com.github.stilllogic20.bedrocktools.client.ClientProxy;
import com.github.stilllogic20.bedrocktools.common.init.Items;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler {

  private Minecraft minecraft = Minecraft.getMinecraft();

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (minecraft.player == null) return;
    ItemStack itemInMainHand = minecraft.player.getHeldItemMainhand();
    if (ClientProxy.keyToggleVein.isPressed()
      && minecraft.player.isSneaking()
      && itemInMainHand.getItem() instanceof ItemBedrockPickaxe) {
      ItemBedrockPickaxe pickaxe = Items.BEDROCK_PICKAXE;
      ItemBedrockPickaxe.VeinMode mode = pickaxe.getVeinMode(itemInMainHand).next();
      pickaxe.setMode(itemInMainHand, null, mode);
      minecraft.player.sendMessage(new TextComponentString(
        String.format("[BedrockTools] %s: %s%s(%.0f)",
          net.minecraft.util.text.translation.I18n
            .translateToLocal("bedrocktools.item.tooltip.veinmode"),
          TextFormatting.BLUE,
          net.minecraft.util.text.translation.I18n
            .translateToLocal("bedrocktools.mode." + mode.name().toLowerCase()),
          mode.getRange())));
    }
  }

}
