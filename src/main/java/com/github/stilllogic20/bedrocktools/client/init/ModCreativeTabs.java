package com.github.stilllogic20.bedrocktools.client.init;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.init.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModCreativeTabs {

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(BedrockToolsMod.MODID) {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(Blocks.BEDROCK);
        }

    };

    public static void init() {
        Items.BEDROCK_PICKAXE.setCreativeTab(CREATIVE_TAB);
        Items.BEDROCK_SWORD.setCreativeTab(CREATIVE_TAB);
        Items.END_PORTAL.setCreativeTab(CREATIVE_TAB);
        Items.PORTAL.setCreativeTab(CREATIVE_TAB);
    }

    private ModCreativeTabs() {}

}
