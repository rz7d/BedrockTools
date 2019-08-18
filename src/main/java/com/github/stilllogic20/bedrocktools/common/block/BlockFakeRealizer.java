package com.github.stilllogic20.bedrocktools.common.block;

import com.github.stilllogic20.bedrocktools.BedrockToolsMod;
import com.github.stilllogic20.bedrocktools.common.init.GUIs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFakeRealizer extends Block {

    public BlockFakeRealizer() {
        super(Material.ROCK);
        setRegistryName(BedrockToolsMod.MODID, "fake_realizer");
        setTranslationKey("Compensation Service");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        playerIn.openGui(BedrockToolsMod.instance, GUIs.ID_FAKE_REALIZER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

}
