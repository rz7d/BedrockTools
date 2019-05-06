package com.github.stilllogic20.bedrocktools.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockFinder {

    private static final EnumFacing[] FACINGS = EnumFacing.values();

    public static boolean equals(@Nullable Block found, @Nullable Block finding) {
        boolean equal = Objects.equals(found, finding);
        if (Objects.equals(finding, Blocks.REDSTONE_ORE) || Objects.equals(finding, Blocks.LIT_REDSTONE_ORE))
            equal |= Objects.equals(found, Blocks.REDSTONE_ORE) || Objects.equals(found, Blocks.LIT_REDSTONE_ORE);
        return equal;
    }

    public static boolean isOre(@Nullable Block block) {
        if (block == null)
            return false;
        if (block == Blocks.LIT_REDSTONE_ORE)
            return true;
        ItemStack stack = new ItemStack(block);
        if (stack.isEmpty())
            return false;
        return Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName)
                .anyMatch(name -> name.startsWith("ore") || name.equals("logWood") || name.equals("treeLeaves"));
    }

    @Nonnull
    public static BlockFinder of(@Nonnull Block target, @Nonnull int max, @Nonnull World world,
            @Nonnull BlockPos origin) {
        return new BlockFinder(
                Objects.requireNonNull(target),
                Objects.requireNonNull(max),
                Objects.requireNonNull(world),
                Objects.requireNonNull(origin));
    }

    private final Block target;
    private final World world;
    private final int max;
    private final BlockPos origin;

    private BlockFinder(@Nonnull Block target, @Nonnull int max, @Nonnull World world, @Nonnull BlockPos origin) {
        this.target = target;
        this.max = max;
        this.world = world;
        this.origin = origin;
    }

    @Nonnull
    public Set<BlockPos> find() {
        Set<BlockPos> found = new HashSet<>(max * FACINGS.length);
        compute(found, origin);
        return found;
    }

    private void compute(@Nonnull Set<BlockPos> found, @Nonnull BlockPos position) {
        if (found.size() >= max || found.contains(position))
            return;
        if (equals(world.getBlockState(position).getBlock(), target)) {
            found.add(position);
            for (EnumFacing facing : FACINGS) {
                compute(found, position.offset(facing));
            }
        }
    }

}
