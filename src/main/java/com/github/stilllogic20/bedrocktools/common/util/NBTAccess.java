package com.github.stilllogic20.bedrocktools.common.util;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTAccess {

    @Nonnull
    private final ItemStack item;
    @Nullable
    private final NBTTagCompound tags;

    public NBTAccess(@Nonnull ItemStack item) {
        this(item, item.getTagCompound());
    }

    public NBTAccess(@Nonnull ItemStack item, @Nullable NBTTagCompound tags) {
        this.item = item;
        this.tags = tags;
    }

    @Nonnull
    public NBTAccess resetNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        item.setTagCompound(nbt);
        return new NBTAccess(item, nbt);
    }

    @Nonnull
    public NBTAccess prepare() {
        if (tags == null)
            return resetNBT();
        return this;
    }

    @Nonnull
    public Optional<? extends NBTAccess> resolve(String key) {
        return has(key)
                ? Optional.of(new NBTAccess(item, tags.getCompoundTag(key)))
                : Optional.empty();
    }

    @Nonnull
    public <T extends Enum<T>> Optional<T> getEnum(String key, @Nonnull T[] values) {
        if (!has(key))
            return Optional.empty();
        int index = getInt(key);
        if (index >= values.length)
            index = 0;
        return Optional.of(values[index]);
    }

    @Nonnull
    public <T extends Enum<T>> boolean setEnum(String key, @Nonnull T value) {
        if (!has(key))
            return false;
        tags.setInteger(key, value.ordinal());
        return true;
    }

    public int getInt(String key) {
        return has(key) ? tags.getInteger(key) : 0;
    }

    public boolean has(String key) {
        return tags != null && tags.hasKey(key);
    }

    public boolean compareAndSet(String key, NBTTagCompound expected, NBTTagCompound value) {
        if (Objects.equals(tags.getCompoundTag(key), expected)) {
            tags.setTag(key, value);
            return true;
        }
        return false;
    }

}