package com.wdcftgg.astralaltar.event;


import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.AddedActiveCraftingTask;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CrystalActiveEvent {

    @SubscribeEvent
    public static void onCrystalActive(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (event.getHand() != EnumHand.MAIN_HAND)
            return;

        ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
        TileCollectorCrystal crystal = MiscUtils.getTileAt(event.getWorld(), event.getPos(), TileCollectorCrystal.class, true);

        if (crystal != null && itemStack.getItem() instanceof ItemWand) {
            TileGodAltar godAltar = GodRecipe.findOuterConstellationAltar(crystal);
            if (GodRecipe.isCrafting(godAltar)) {
                AddedActiveCraftingTask task = godAltar.getAddedActiveCraftingTask();
                if (task != null) {
                    int time = task.getTotalCraftingTime();
                    if (time >= GodRecipe.constellationBegin && time <= GodRecipe.constellationEnd) {
                        if (!player.world.isRemote) {
                            GodRecipe.setCrystalActive(crystal, !GodRecipe.isCrystalActive(crystal));
                        } else {
                            player.swingArm(EnumHand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }
}
