package com.wdcftgg.astralaltar.crafting;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import net.minecraft.nbt.NBTTagCompound;

public interface IAddedCraftingProgress {
    public boolean tryProcess(TileGodAltar altar, AddedActiveCraftingTask runningTask, NBTTagCompound craftingData, int activeCraftingTick, int totalCraftingTime);
    public boolean tryProcess(TileGodAltar altar, NBTTagCompound craftingData, int activeCraftingTick, int totalCraftingTime);

}
