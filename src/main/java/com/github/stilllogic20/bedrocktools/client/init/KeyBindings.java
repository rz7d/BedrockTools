package com.github.stilllogic20.bedrocktools.client.init;

import com.github.stilllogic20.bedrocktools.common.init.Items;
import com.github.stilllogic20.bedrocktools.common.init.Messages;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.network.VeinModeChangedMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyBindings {

    public static final KeyBinding keyToggleVein = new KeyBinding(
            "bedrocktools.keybinding.togglevein", -98, "BedrockTools");

    public static void init() {
        ClientRegistry.registerKeyBinding(keyToggleVein);
        MinecraftForge.EVENT_BUS.register(new KeyBindings());
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        ItemStack itemInMainHand = mc.player.getHeldItemMainhand();
        if (keyToggleVein.isPressed()
                && mc.player.isSneaking()
                && itemInMainHand.getItem() instanceof ItemBedrockPickaxe) {
            ItemBedrockPickaxe pickaxe = Items.BEDROCK_PICKAXE;
            ItemBedrockPickaxe.VeinMode newMode = pickaxe.getVeinMode(itemInMainHand).next();
            Messages.NETWORK.sendToServer(new VeinModeChangedMessage(newMode));
            mc.player.sendMessage(new TextComponentString(
                    String.format("[BedrockTools] %s: %s%s(%.0f)",
                            I18n.format("bedrocktools.item.tooltip.veinmode"),
                            TextFormatting.DARK_AQUA,
                            I18n.format("bedrocktools.mode." + newMode.name().toLowerCase()),
                            newMode.getRange())));
        }
    }

}
