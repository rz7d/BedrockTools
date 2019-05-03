package com.github.stilllogic20.bedrocktools.common;

import com.github.stilllogic20.bedrocktools.common.init.Items;
import com.github.stilllogic20.bedrocktools.common.init.Messages;
import com.github.stilllogic20.bedrocktools.common.init.Recipes;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class CommonProxy {

    public void construct(FMLConstructionEvent event) {
        final EventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(new Items());
        eventBus.register(new Recipes());
        Messages.init();
    }

}
