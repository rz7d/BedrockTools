package com.github.stilllogic20.bedrocktools.common.merchant;

import com.github.stilllogic20.bedrocktools.common.compat.XUCompat;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class MerchantRealizer implements IMerchant {

    private static final MerchantRecipeList RECIPES;

    static {
        RECIPES = new MerchantRecipeList() {
            @Nullable
            @Override
            public MerchantRecipe canRecipeBeUsed(ItemStack item0, ItemStack item1, int index) {
                Optional<ItemStack> item = Stream.of(item0, item1)
                    .filter(s -> {
                        ResourceLocation registryName = s.getItem().getRegistryName();
                        if (registryName == null)
                            return false;
                        return registryName.getNamespace().equals("extrautils2") && registryName.getPath().equals("fakecopy");
                    })
                    .findFirst();
                return item.map(fake -> {
                    ItemStack original = XUCompat.ItemFakeCopy.getOriginalStack(fake.getItemDamage()).copy();
                    original.setCount(fake.getCount());
                    return new MerchantRecipe(fake, ItemStack.EMPTY, original);
                }).orElseGet(() -> super.canRecipeBeUsed(item0, item1, index));
            }
        };
    }

    private final World world;
    private final EntityPlayer player;

    private EntityPlayer customer;

    public MerchantRealizer(World world, EntityPlayer player) {
        this.world = world;
        this.player = player;
        setCustomer(player);
    }

    @Override
    public void setCustomer(@Nullable EntityPlayer player) {
        customer = player;
    }

    @Nullable
    @Override
    public EntityPlayer getCustomer() {
        return customer;
    }

    @Nullable
    @Override
    public MerchantRecipeList getRecipes(EntityPlayer player) {
        return RECIPES;
    }

    @Override
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {

    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {

    }

    @Override
    public void verifySellingItem(ItemStack stack) {
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("Compensation Service");
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public BlockPos getPos() {
        return player.getPosition();
    }

}
