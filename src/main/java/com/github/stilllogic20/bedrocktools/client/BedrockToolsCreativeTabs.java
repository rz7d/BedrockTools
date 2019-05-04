package com.github.stilllogic20.bedrocktools.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BedrockToolsCreativeTabs extends CreativeTabs {

    public BedrockToolsCreativeTabs(String group) {
        super(group);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.BEDROCK);
    }

}
