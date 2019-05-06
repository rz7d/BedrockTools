package com.github.stilllogic20.bedrocktools.common;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BedrockToolsMaterial {

    public static final ToolMaterial BEDROCK;
    static {
        BEDROCK = EnumHelper.addToolMaterial("BEDROCK", -1, -1, 18, 22, 5);
    }

}
