package com.github.stilllogic20.bedrocktools.client.init;

import com.github.stilllogic20.bedrocktools.common.init.Blocks;
import com.github.stilllogic20.bedrocktools.common.init.Items;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public final class Renders {

    private Renders() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Renders());
    }

    private static void registerItemToRender(@Nonnull Item item) {
        final ResourceLocation registryName = item.getRegistryName();

        if (registryName == null)
            throw new IllegalArgumentException("item.getRegistryName() cannot be null");
        ModelLoader.setCustomModelResourceLocation(item, 0,
            new ModelResourceLocation(registryName, "inventory"));
    }

    @SubscribeEvent
    public void registerModels(@Nullable ModelRegistryEvent event) {
        registerItemToRender(Items.BEDROCK_PICKAXE);
        registerItemToRender(Items.BEDROCK_SWORD);
        registerItemToRender(Items.PORTAL);
        registerItemToRender(Items.END_PORTAL);
        registerItemToRender(Blocks.FAKE_REALIZER_ITEM);
    }

}
