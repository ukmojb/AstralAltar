package com.wdcftgg.astralaltar.gui;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ConstellationFocusSlot extends SlotItemHandler {

    private final TileGodAltar ta;

    public ConstellationFocusSlot(IItemHandler itemHandler, TileGodAltar ta, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.ta = ta;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        ItemStack currentStack = super.getStack();
        if (!ItemStack.areItemStacksEqual(currentStack, ta.getFocusItem())) {
            ta.setFocusStack(currentStack.isEmpty() ? ItemStack.EMPTY : currentStack.copy());
        }
    }
}
