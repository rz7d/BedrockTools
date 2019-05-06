package com.github.stilllogic20.bedrocktools;

import javax.annotation.Nonnull;

import com.github.stilllogic20.bedrocktools.common.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;

@Mod(modid = BedrockToolsMod.MODID, acceptableRemoteVersions = "*")
public final class BedrockToolsMod {

    public static final String MODID = "bedrocktools";

    @SidedProxy(modId = MODID, clientSide = "com.github.stilllogic20.bedrocktools.client.ClientProxy", serverSide = "com.github.stilllogic20.bedrocktools.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void construct(@Nonnull FMLConstructionEvent event) {
        proxy.construct(event);
    }

}
