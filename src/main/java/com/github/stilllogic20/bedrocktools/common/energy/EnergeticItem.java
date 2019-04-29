package com.github.stilllogic20.bedrocktools.common.energy;

import net.minecraft.item.Item;

/**
 * Unit is BedrockPower (BP)
 */
public class EnergeticItem extends Item {

    private final long capacity;

    private volatile long current;

    protected EnergeticItem(long energyCapacity) {
        this.capacity = energyCapacity;
    }

    public void charge(long input) {
        this.current += input;
    }

    public void output(long output) {
        this.current -= output;
    }

    public long capacity() {
        return capacity;
    }

    public long current() {
        return current;
    }

}
