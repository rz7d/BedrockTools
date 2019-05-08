package com.github.stilllogic20.bedrocktools.common.item;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

public class ItemPortal extends ItemBlock {

    private static final String NAME = "portal";

    public ItemPortal() {
        super(Blocks.PORTAL);
        setTranslationKey("portal");
        setRegistryName(BedrockToolsMod.MODID, NAME);
    }

}
