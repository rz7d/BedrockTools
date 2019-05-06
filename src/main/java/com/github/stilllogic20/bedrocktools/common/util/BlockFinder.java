package com.github.stilllogic20.bedrocktools.common.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFinder {

    private static final EnumFacing[] FACINGS = EnumFacing.values();

    public static boolean equals(Block found, Block finding) {
        boolean equal = (found == finding);
        if (finding == Blocks.REDSTONE_ORE || finding == Blocks.LIT_REDSTONE_ORE)
            equal |= found == Blocks.REDSTONE_ORE || found == Blocks.LIT_REDSTONE_ORE;
        return equal;
    }

    public static BlockFinder of(Block target, int max, World world, BlockPos origin) {
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

    private BlockFinder(Block target, int max, World world, BlockPos origin) {
        this.target = target;
        this.max = max;
        this.world = world;
        this.origin = origin;
    }

    public Set<BlockPos> find() {
        Set<BlockPos> found = new HashSet<>(max * FACINGS.length);
        compute(found, max, world, origin);
        return found;
    }

    private void compute(Set<BlockPos> found, int max, World world, BlockPos position) {
        if (found.size() >= max || found.contains(position))
            return;
        if (equals(world.getBlockState(position).getBlock(), target)) {
            found.add(position);
            for (EnumFacing facing : FACINGS) {
                compute(found, max, world, position.offset(facing));
            }
        }
    }

}
