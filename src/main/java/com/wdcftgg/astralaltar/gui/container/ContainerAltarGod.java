package com.wdcftgg.astralaltar.gui.container;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.gui.ConstellationFocusSlot;
import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAltarGod extends Container {
    public final InventoryPlayer playerInv;
    public final TileGodAltar tileAltar;
    public final ItemStackHandler invHandler;
    public final int altarGridSlotSize;

    protected ConstellationFocusSlot focusSlot;

    public ContainerAltarGod(InventoryPlayer playerInv, TileGodAltar tileAltar) {
        this.playerInv = playerInv;
        this.tileAltar = tileAltar;
        this.invHandler = tileAltar.getInventoryHandler();
        this.altarGridSlotSize = 42;
        this.bindPlayerInventory();
        this.bindAltarInventory();
    }


    public void bindAltarInventory() {
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new SlotItemHandler(invHandler,     xx, 120 + xx * 18, 47));
        }
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new SlotItemHandler(invHandler, 3 + xx, 120 + xx * 18, 65));
        }
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new SlotItemHandler(invHandler, 6 + xx, 120 + xx * 18, 83));
        }
        addSlotToContainer(new SlotItemHandler(invHandler,  9, 102, 29));
        addSlotToContainer(new SlotItemHandler(invHandler, 10, 174, 29));
        addSlotToContainer(new SlotItemHandler(invHandler, 11, 102, 101));
        addSlotToContainer(new SlotItemHandler(invHandler, 12, 174, 101));

        addSlotToContainer(new SlotItemHandler(invHandler, 13, 120,  29));
        addSlotToContainer(new SlotItemHandler(invHandler, 14, 156,  29));
        addSlotToContainer(new SlotItemHandler(invHandler, 15, 102,  47));
        addSlotToContainer(new SlotItemHandler(invHandler, 16, 174,  47));
        addSlotToContainer(new SlotItemHandler(invHandler, 17, 102,  83));
        addSlotToContainer(new SlotItemHandler(invHandler, 18, 174,  83));
        addSlotToContainer(new SlotItemHandler(invHandler, 19, 120,  101));
        addSlotToContainer(new SlotItemHandler(invHandler, 20, 156,  101));

        addSlotToContainer(new SlotItemHandler(invHandler, 21, 138,  29)); //Up center
        addSlotToContainer(new SlotItemHandler(invHandler, 22, 102,  65)); //Left center
        addSlotToContainer(new SlotItemHandler(invHandler, 23, 174,  65)); //Right center
        addSlotToContainer(new SlotItemHandler(invHandler, 24, 138,  101)); //Lower center

        this.focusSlot = new ConstellationFocusSlot(invHandler, tileAltar, 25, 34, 30);
        addSlotToContainer(this.focusSlot);
//        addSlotToContainer(new SlotItemHandler(invHandler,25, 34, 30));


        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new SlotItemHandler(invHandler,26 + xx, 120 + xx * 18, 11));
        }
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new SlotItemHandler(invHandler,29 + xx, 120 + xx * 18, 119));
        }
        for (int yy = 0; yy < 3; yy++) {
            addSlotToContainer(new SlotItemHandler(invHandler,32 + yy, 84, 47 + yy * 18));
        }
        for (int yy = 0; yy < 3; yy++) {
            addSlotToContainer(new SlotItemHandler(invHandler,35 + yy, 192, 47 + yy * 18));
        }

        addSlotToContainer(new SlotItemHandler(invHandler, 38, 84,  11)); //Top left corner
        addSlotToContainer(new SlotItemHandler(invHandler, 39, 192,  11)); //Top right corner
        addSlotToContainer(new SlotItemHandler(invHandler, 40, 84,  119)); //Lower left corner
        addSlotToContainer(new SlotItemHandler(invHandler, 41, 192,  119)); //Lower right corner


    }

    public void bindPlayerInventory() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(this.playerInv, j + i * 9 + 9, 66 + j * 18, 156 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(this.playerInv, i, 66 + i * 18, 214));
        }
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index >= 0 && index < 36 &&
                    itemstack1.getItem() instanceof ItemConstellationFocus &&
                    ((ItemConstellationFocus)itemstack1.getItem()).getFocusConstellation(itemstack1) != null &&
                    this.mergeItemStack(itemstack1, 25, 25, false)) {
                return itemstack;
            }

            if (index >= 0 && index < 27) {
                if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return itemstack;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public boolean canInteractWith(EntityPlayer player) {
        BlockPos pos = this.tileAltar.getPos();
        if (this.tileAltar.getWorld().getTileEntity(pos) != this.tileAltar) {
            return false;
        } else {
            return player.getDistanceSq((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public Slot getSlot(int slotId) {
        if (slotId < 0 || slotId >= this.inventorySlots.size()) {
            System.err.println("Attempted to access invalid slot: " + slotId + ", max: " + (this.inventorySlots.size() - 1));
            return new Slot(Minecraft.getMinecraft().player.inventory, 0, 0, 0) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn) { return false; }
                @Override
                public boolean isItemValid(ItemStack stack) { return false; }
            };
        }
        return super.getSlot(slotId);
    }


}
