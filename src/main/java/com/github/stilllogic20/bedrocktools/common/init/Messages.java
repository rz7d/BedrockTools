package com.github.stilllogic20.bedrocktools.common.init;

import com.github.stilllogic20.bedrocktools.common.network.VeinModeChangedMessage;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Messages {

    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper("bedrocktools");

    public static void init() {
        NETWORK.registerMessage(VeinModeChangedMessage.class, VeinModeChangedMessage.class, 0, Side.SERVER);
    }

}
