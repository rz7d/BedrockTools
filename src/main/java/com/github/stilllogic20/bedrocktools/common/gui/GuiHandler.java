package com.github.stilllogic20.bedrocktools.common.gui;

import com.github.stilllogic20.bedrocktools.common.init.GUIs;
import com.github.stilllogic20.bedrocktools.common.merchant.MerchantRealizer;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUIs.ID_FAKE_REALIZER:
                return new ContainerMerchant(
                    player.inventory,
                    new MerchantRealizer(
                        world, player
                    ),
                    world
                );
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUIs.ID_FAKE_REALIZER:
                return new GuiMerchant(
                    player.inventory,
                    new MerchantRealizer(
                        world, player
                    ),
                    world
                );
        }
        return null;
    }

}
