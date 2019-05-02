package com.github.stilllogic20.bedrocktools.client;

import com.github.stilllogic20.bedrocktools.client.init.ClientTickHandler;
import com.github.stilllogic20.bedrocktools.client.init.CreativeTabs;
import com.github.stilllogic20.bedrocktools.client.init.Renders;
import com.github.stilllogic20.bedrocktools.common.CommonProxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  public static final KeyBinding keyToggleVein = new KeyBinding("bedrocktools.keybinding.togglevein", -98, "BedrockTools");

    @Override
    public void construct(FMLConstructionEvent event) {
        super.construct(event);
        ClientRegistry.registerKeyBinding(keyToggleVein);
        Stream
          .of(new Renders(),new ClientTickHandler())
          .forEach(MinecraftForge.EVENT_BUS::register);
        CreativeTabs.init();
    }

}
