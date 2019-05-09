package com.github.stilllogic20.bedrocktools.common.init;

import com.github.stilllogic20.bedrocktools.common.network.CPacketVeinModeChanged;
import com.github.stilllogic20.bedrocktools.common.network.SPacketMiningModeChanged;
import com.github.stilllogic20.bedrocktools.common.network.SPacketWeaponModeChanged;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.nio.channels.NetworkChannel;

public class Messages {

    public static final SimpleNetworkWrapper C_PICKAXE_NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("bedrocktools.client.pickaxe");
    public static final SimpleNetworkWrapper S_PICKAXE_NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("bedrocktools.server.pickaxe");
    public static final SimpleNetworkWrapper S_SWORD_NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("bedrocktools.server.sword");
    private Messages() {
    }

    public static void init() {
        C_PICKAXE_NETWORK.registerMessage(CPacketVeinModeChanged.class, CPacketVeinModeChanged.class, 0, Side.SERVER);
        S_PICKAXE_NETWORK.registerMessage(SPacketMiningModeChanged.class, SPacketMiningModeChanged.class, 0, Side.CLIENT);
        S_SWORD_NETWORK.registerMessage(SPacketWeaponModeChanged.class, SPacketWeaponModeChanged.class, 0, Side.CLIENT);
    }

}
