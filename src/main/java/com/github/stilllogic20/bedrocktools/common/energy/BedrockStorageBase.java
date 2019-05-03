package com.github.stilllogic20.bedrocktools.common.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class BedrockStorageBase implements IEnergyStorage {

    private final long capacity;

    private volatile long stored;

    protected BedrockStorageBase(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int toReceive = (int) Math.min(maxReceive, stored);
        stored += toReceive;
        return toReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int toExtract = (int) Math.min(maxExtract, stored);
        stored -= toExtract;
        return toExtract;
    }

    @Override
    public int getEnergyStored() {
        return (int) stored;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

}
