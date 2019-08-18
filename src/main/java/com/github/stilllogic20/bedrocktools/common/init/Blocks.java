package com.github.stilllogic20.bedrocktools.common.init;

import com.github.stilllogic20.bedrocktools.common.block.BlockFakeRealizer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public final class Blocks {

    public static final BlockFakeRealizer FAKE_REALIZER;
    public static final ItemBlock FAKE_REALIZER_ITEM;

    static {
        FAKE_REALIZER = new BlockFakeRealizer();
        FAKE_REALIZER_ITEM = new ItemBlock(FAKE_REALIZER);
        FAKE_REALIZER_ITEM.setRegistryName(Objects.requireNonNull(FAKE_REALIZER.getRegistryName()));
    }

    private Blocks() {
    }


    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Blocks());
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(FAKE_REALIZER);
    }

    @SubscribeEvent
    public void registerBlockItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(FAKE_REALIZER_ITEM);
    }

}
