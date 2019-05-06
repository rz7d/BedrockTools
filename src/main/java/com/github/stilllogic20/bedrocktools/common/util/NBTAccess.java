package com.github.stilllogic20.bedrocktools.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
        final ItemStack item = this.item;
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

    public Optional<? extends NBTAccess> resolve(@Nullable String key) {
        final NBTTagCompound tags = this.tags;
        return tags != null && has(key)
                ? Optional.of(new NBTAccess(item, tags.getCompoundTag(key)))
                : Optional.empty();
    }

    @Nonnull
    public <T extends Enum<T>> Optional<T> getEnum(@Nullable String key, @Nonnull T[] values) {
        if (!has(key))
            return Optional.empty();
        int index = getInt(key).orElse(0);
        if (index >= values.length)
            index = 0;
        return Optional.of(values[index]);
    }

    public <T extends Enum<T>> boolean setEnum(@Nullable String key, @Nonnull T value) {
        final NBTTagCompound tags = this.tags;
        if (tags == null)
            return false;
        tags.setInteger(key, value.ordinal());
        return true;
    }

    public OptionalInt getInt(@Nullable String key) {
        final NBTTagCompound tags = this.tags;
        if (tags == null)
            return OptionalInt.empty();
        return has(key) ? OptionalInt.of(tags.getInteger(key)) : OptionalInt.empty();
    }

    public boolean has(@Nullable String key) {
        final NBTTagCompound tags = this.tags;
        return tags != null && tags.hasKey(key);
    }

    public boolean compareAndSet(@Nullable String key, @Nullable NBTTagCompound expected,
            @Nullable NBTTagCompound value) {
        final NBTTagCompound tags = this.tags;
        if (tags != null && Objects.equals(tags.getCompoundTag(key), expected)) {
            tags.setTag(key, value);
            return true;
        }
        return false;
    }

}