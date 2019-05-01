package com.github.stilllogic20.bedrocktools.common.init;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockSword;
import com.github.stilllogic20.bedrocktools.common.item.ItemEndPortal;
import com.github.stilllogic20.bedrocktools.common.item.ItemPortal;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class Items {

    public static final ItemBedrockPickaxe BEDROCK_PICKAXE;
    public static final ItemBedrockSword BEDROCK_SWORD;

    public static final Item PORTAL;
    public static final Item END_PORTAL;

    static {
        BEDROCK_PICKAXE = new ItemBedrockPickaxe();
        BEDROCK_SWORD = new ItemBedrockSword();
        PORTAL = new ItemPortal();
        END_PORTAL = new ItemEndPortal();
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(BEDROCK_PICKAXE);
        registry.register(BEDROCK_SWORD);
        registry.register(PORTAL);
        registry.register(END_PORTAL);
    }

}
