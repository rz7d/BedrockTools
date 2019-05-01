package com.github.stilllogic20.bedrocktools.client.init;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.client.BedrockToolsCreativeTabs;
import com.github.stilllogic20.bedrocktools.common.init.Items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreativeTabs {

    public static final net.minecraft.creativetab.CreativeTabs CREATIVE_TAB = new BedrockToolsCreativeTabs(
            BedrockToolsMod.MODID);

    public static void init() {
        Items.BEDROCK_PICKAXE.setCreativeTab(CREATIVE_TAB);
        Items.BEDROCK_SWORD.setCreativeTab(CREATIVE_TAB);
        Items.END_PORTAL.setCreativeTab(CREATIVE_TAB);
        Items.PORTAL.setCreativeTab(CREATIVE_TAB);
    }

}
