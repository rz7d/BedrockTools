package com.github.stilllogic20.bedrocktools.common.init;

import com.github.stilllogic20.bedrocktools.common.network.CPacketVeinModeChanged;
import com.github.stilllogic20.bedrocktools.common.network.SPacketMiningModeChanged;
import com.github.stilllogic20.bedrocktools.common.network.SPacketWeaponModeChanged;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Messages {

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("bedrocktools");
    private Messages() {
    }

    public static void init() {
        NETWORK.registerMessage(CPacketVeinModeChanged.Handler.class, CPacketVeinModeChanged.class, 0, Side.SERVER);
        NETWORK.registerMessage(SPacketMiningModeChanged.Handler.class, SPacketMiningModeChanged.class, 1, Side.CLIENT);
        NETWORK.registerMessage(SPacketWeaponModeChanged.Handler.class, SPacketWeaponModeChanged.class, 2, Side.CLIENT);
    }

}
