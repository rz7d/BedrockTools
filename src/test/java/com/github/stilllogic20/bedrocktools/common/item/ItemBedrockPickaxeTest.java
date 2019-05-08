package com.github.stilllogic20.bedrocktools.common.item;

import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.MiningMode;
import com.github.stilllogic20.bedrocktools.common.item.ItemBedrockPickaxe.VeinMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemBedrockPickaxeTest {

    @Test
    public void testMiningModeNext() {
        assertEquals(MiningMode.MIDDLE, MiningMode.NORMAL.next());
        assertEquals(MiningMode.SLOW, MiningMode.MIDDLE.next());
        assertEquals(MiningMode.FAST, MiningMode.SLOW.next());
        assertEquals(MiningMode.INSANE, MiningMode.FAST.next());
        assertEquals(MiningMode.OFF, MiningMode.INSANE.next());
        assertEquals(MiningMode.NORMAL, MiningMode.OFF.next());
    }

    @Test
    public void testVeinModeNext() {
        assertEquals(VeinMode.MORE, VeinMode.NORMAL.next());
        assertEquals(VeinMode.ALL, VeinMode.MORE.next());
        assertEquals(VeinMode.OFF, VeinMode.ALL.next());
        assertEquals(VeinMode.NORMAL, VeinMode.OFF.next());
    }

}
