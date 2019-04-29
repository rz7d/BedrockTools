package com.github.stilllogic20.bedrocktools.common.item;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.ItemMode;

public class ItemBedrockPickaxeTest {

    @Test
    public void testNext() {
        assertEquals(ItemMode.MIDDLE, ItemMode.NORMAL.next());
        assertEquals(ItemMode.SLOW, ItemMode.MIDDLE.next());
        assertEquals(ItemMode.FAST, ItemMode.SLOW.next());
        assertEquals(ItemMode.INSANE, ItemMode.FAST.next());
        assertEquals(ItemMode.OFF, ItemMode.INSANE.next());
        assertEquals(ItemMode.NORMAL, ItemMode.OFF.next());
    }

}
