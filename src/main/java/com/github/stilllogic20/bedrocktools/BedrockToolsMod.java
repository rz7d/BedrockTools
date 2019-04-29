package com.github.stilllogic20.bedrocktools;

import com.github.stilllogic20.bedrocktools.common.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;

@Mod(modid = BedrockToolsMod.MODID, name = BedrockToolsMod.NAME, version = BedrockToolsMod.VERSION)
public final class BedrockToolsMod {

    public static final String MODID = "bedrocktools";
    public static final String NAME = "BedrockTools";
    public static final String VERSION = "1.1-beta-";

    @SidedProxy(modId = MODID, clientSide = "com.github.stilllogic20.bedrocktools.client.ClientProxy", serverSide = "com.github.stilllogic20.bedrocktools.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        proxy.construct(event);
    }

}
