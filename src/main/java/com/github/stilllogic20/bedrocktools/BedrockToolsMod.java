package com.github.stilllogic20.bedrocktools;

import com.github.stilllogic20.bedrocktools.common.CommonProxy;
import com.github.stilllogic20.bedrocktools.common.gui.GuiHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;

@Mod(modid = BedrockToolsMod.MODID, acceptableRemoteVersions = "*")
public final class BedrockToolsMod {

    public static final String MODID = "bedrocktools";

    public static BedrockToolsMod instance;

    public BedrockToolsMod() {
        instance = this;
    }

    @SidedProxy(modId = MODID, clientSide = "com.github.stilllogic20.bedrocktools.client.ClientProxy", serverSide = "com.github.stilllogic20.bedrocktools.common.CommonProxy")
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void construct(@Nonnull FMLConstructionEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        proxy.construct(event);
    }

}
