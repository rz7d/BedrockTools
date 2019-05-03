package com.github.stilllogic20.bedrocktools.common.item;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.MiningMode;

public class ItemBedrockPickaxeTest {

    @Test
    public void testNext() {
        assertEquals(MiningMode.MIDDLE, MiningMode.NORMAL.next());
        assertEquals(MiningMode.SLOW, MiningMode.MIDDLE.next());
        assertEquals(MiningMode.FAST, MiningMode.SLOW.next());
        assertEquals(MiningMode.INSANE, MiningMode.FAST.next());
        assertEquals(MiningMode.OFF, MiningMode.INSANE.next());
        assertEquals(MiningMode.NORMAL, MiningMode.OFF.next());
    }

}
