package com.github.stilllogic20.bedrocktools.common.item;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

public class ItemEndPortal extends ItemBlock {

    private static final String NAME = "end_portal";

    public ItemEndPortal() {
        super(Blocks.END_PORTAL);
        setTranslationKey("end_portal");
        setRegistryName(BedrockToolsMod.MODID, NAME);
    }

}
