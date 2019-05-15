package com.github.stilllogic20.bedrocktools.client.init;

import com.github.stilllogic20.bedrocktools.common.init.Messages;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.VeinMode;
import com.github.stilllogic20.bedrocktools.common.network.CPacketVeinModeChanged;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.WHITE;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class KeyBindings {

    private static final KeyBinding KEY_TOGGLE_VEIN = new KeyBinding(
        "bedrocktools.keybinding.togglevein", -98, "BedrockTools");
    private final Minecraft mc = Minecraft.getMinecraft();

    private KeyBindings() {
    }

    public static void init() {
        ClientRegistry.registerKeyBinding(KEY_TOGGLE_VEIN);
        MinecraftForge.EVENT_BUS.register(new KeyBindings());
    }

    @SubscribeEvent
    public void onKeyInput(@Nonnull InputEvent.KeyInputEvent event) {
        final ItemStack itemInMainHand = mc.player.getHeldItemMainhand();
        if (KEY_TOGGLE_VEIN.isPressed()
            && mc.player.isSneaking()
            && itemInMainHand.getItem() instanceof ItemBedrockPickaxe) {
            VeinMode newMode = ItemBedrockPickaxe.getVeinMode(itemInMainHand).next();
            Messages.NETWORK.sendToServer(new CPacketVeinModeChanged(newMode));
            mc.player.sendMessage(new TextComponentString(
                String.format("%s[%sBedrockTools%s]%s %s: %s%s(%d)",
                    DARK_GRAY, GRAY, DARK_GRAY, WHITE,
                    I18n.format("bedrocktools.item.tooltip.veinmode"),
                    TextFormatting.DARK_AQUA,
                    I18n.format("bedrocktools.mode." + newMode.name().toLowerCase()),
                    newMode.range())));
        }
    }

}
