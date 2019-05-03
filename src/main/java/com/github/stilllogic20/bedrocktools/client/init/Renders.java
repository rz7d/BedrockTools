package com.github.stilllogic20.bedrocktools.client.init;

import java.util.Objects;

import com.github.stilllogic20.bedrocktools.common.init.Items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Renders {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Renders());
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerToRender(Items.BEDROCK_PICKAXE);
        registerToRender(Items.BEDROCK_SWORD);
        registerToRender(Items.PORTAL);
        registerToRender(Items.END_PORTAL);
    }

    private static void registerToRender(Item item) {
        Objects.requireNonNull(item);
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

}
