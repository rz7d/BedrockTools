package com.github.stilllogic20.bedrocktools.client;

import com.github.stilllogic20.bedrocktools.client.init.CreativeTabs;
import com.github.stilllogic20.bedrocktools.client.init.KeyBindings;
import com.github.stilllogic20.bedrocktools.client.init.Renders;
import com.github.stilllogic20.bedrocktools.common.CommonProxy;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void construct(FMLConstructionEvent event) {
        super.construct(event);
        Renders.init();
        CreativeTabs.init();
        KeyBindings.init();
    }

}
