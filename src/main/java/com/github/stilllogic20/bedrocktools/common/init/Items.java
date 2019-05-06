package com.github.stilllogic20.bedrocktools.common.init;

import javax.annotation.Nonnull;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockSword;
import com.github.stilllogic20.bedrocktools.common.item.ItemEndPortal;
import com.github.stilllogic20.bedrocktools.common.item.ItemPortal;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class Items {

    @Nonnull
    public static final ItemBedrockPickaxe BEDROCK_PICKAXE;
    @Nonnull
    public static final ItemBedrockSword BEDROCK_SWORD;

    @Nonnull
    public static final Item PORTAL;
    @Nonnull
    public static final Item END_PORTAL;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Items());
    }

    static {
        BEDROCK_PICKAXE = new ItemBedrockPickaxe();
        BEDROCK_SWORD = new ItemBedrockSword();
        PORTAL = new ItemPortal();
        END_PORTAL = new ItemEndPortal();
    }

    private Items() {}

    @SubscribeEvent
    public void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(BEDROCK_PICKAXE);
        registry.register(BEDROCK_SWORD);
        registry.register(PORTAL);
        registry.register(END_PORTAL);
    }

}
