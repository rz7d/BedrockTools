package com.github.stilllogic20.bedrocktools.common;

import com.github.stilllogic20.bedrocktools.common.init.Items;
import com.github.stilllogic20.bedrocktools.common.init.Messages;
import com.github.stilllogic20.bedrocktools.common.init.Recipes;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;

public class CommonProxy {

    public void construct(FMLConstructionEvent event) {
        Items.init();
        Recipes.init();
        Messages.init();
    }

}
