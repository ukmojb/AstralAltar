package com.wdcftgg.astralaltar.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class AddedRecipeAdapter extends InventoryCrafting {

    private static final AdapterContainer emptyContainer = new AdapterContainer();

    public AddedRecipeAdapter(int width, int height) {
        super(emptyContainer, width, height);
    }

    public void fill(ItemStack[] stacks) {
        if(stacks.length != getWidth() * getHeight()) return; //Ugh... ?

        for (int xx = 0; xx < getWidth(); xx++) {
            for (int zz = 0; zz < getHeight(); zz++) {
                setInventorySlotContents(xx * getHeight() + zz, stacks[xx * getHeight() + zz]);
            }
        }
    }

    private static class AdapterContainer extends Container {

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return false;
        }

        @Override
        public void onContainerClosed(EntityPlayer playerIn) {
            super.onContainerClosed(playerIn);
        }

    }

}
