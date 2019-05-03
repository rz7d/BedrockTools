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

    private final Block target;

    public BlockFinder(Block target) {
        Objects.requireNonNull(target);
        this.target = target;
    }

    public Set<BlockPos> find(int max, World world, BlockPos position) {
        Set<BlockPos> found = new HashSet<>(max * FACINGS.length);
        compute(found, max, world, position);
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

    private static boolean equals(Block found, Block finding) {
        if (finding == Blocks.REDSTONE_ORE || finding == Blocks.LIT_REDSTONE_ORE)
            return found == Blocks.REDSTONE_ORE || found == Blocks.LIT_REDSTONE_ORE;
        return found == finding;
    }

}
