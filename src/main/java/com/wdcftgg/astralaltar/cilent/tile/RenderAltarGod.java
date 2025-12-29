package com.wdcftgg.astralaltar.cilent.tile;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.AddedActiveCraftingTask;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.render.tile.TESRCollectorCrystal;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;

public class RenderAltarGod extends TileEntitySpecialRenderer<TileGodAltar> {

    @Override
    public void render(TileGodAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        if(te.getMultiblockState()) {
            IConstellation c = te.getFocusedConstellation();
            if(c != null) {
                GL11.glPushMatrix();
                float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(te.getWorld());
                alphaDaytime *= 0.8F;

                int max = 5000;
                int t = (int) (ClientScheduler.getClientTick() % max);
                float halfAge = max / 2F;
                float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                tr *= 2;

                RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

                float br = 0.9F * alphaDaytime;

                RenderConstellation.renderConstellationIntoWorldFlat(c, c.getConstellationColor(), new Vector3(te).add(0.5, 0.03, 0.5), 5 + tr, 2, 0.1F + br);
                GL11.glPopMatrix();
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 4, z + 0.5);
            AddedActiveCraftingTask act = te.getAddedActiveCraftingTask();
            if(act != null && act.getRecipeToCraft() instanceof GodRecipe) {
                Collection<ItemHandle> requiredHandles = ((GodRecipe) act.getRecipeToCraft()).getTraitItemHandles();
                if(!requiredHandles.isEmpty()) {
                    int amt = 60 / requiredHandles.size();
                    for (ItemHandle outer : requiredHandles) {
                        NonNullList<ItemStack> stacksApplicable = outer.getApplicableItemsForRender();
                        int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                        ItemStack element = stacksApplicable.get(MathHelper.floor(
                                MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                        Color col = ItemColorizationHelper.getDominantColorFromItemStack(element);
                        if(col == null) {
                            col = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                        }
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, col, 0x12315L | outer.hashCode(), ClientScheduler.getClientTick(), 20, 2F, amt, amt / 2);
                    }
                }
                RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.CYAN, 0, ClientScheduler.getClientTick(), 15, 2F, 40, 25);
            } else {
                RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.CYAN, 0x12315661L, ClientScheduler.getClientTick(), 20, 2F, 50, 25);
                RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.WHITE, 0, ClientScheduler.getClientTick(), 10, 1F, 40, 25);
            }
            GlStateManager.translate(0, 0.15, 0);
            GlStateManager.scale(0.7, 0.7, 0.7);
            TESRCollectorCrystal.renderCrystal(null, true, true);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 8, z + 0.5);
            AddedActiveCraftingTask act1 = te.getAddedActiveCraftingTask();
            if(act1 != null && act1.getRecipeToCraft() instanceof GodRecipe) {
                Collection<ItemHandle> requiredHandles = ((GodRecipe) act1.getRecipeToCraft()).getTraitItemHandles();
                if(!requiredHandles.isEmpty()) {
                    int amt = 60 / requiredHandles.size();
                    for (ItemHandle outer : requiredHandles) {
                        NonNullList<ItemStack> stacksApplicable = outer.getApplicableItemsForRender();
                        int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                        ItemStack element = stacksApplicable.get(MathHelper.floor(
                                MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                        Color col = ItemColorizationHelper.getDominantColorFromItemStack(element);
                        if(col == null) {
                            col = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                        }
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, col, 0x12315L | outer.hashCode(), ClientScheduler.getClientTick(), 20, 2F, amt, amt / 2);
                    }
                }
                RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.BLUE, 0, ClientScheduler.getClientTick(), 15, 2F, 40, 25);
            } else {
                RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.BLUE, 0x12315661L, ClientScheduler.getClientTick(), 20, 2F, 50, 25);
                RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.MAGENTA, 0, ClientScheduler.getClientTick(), 10, 1F, 40, 25);
            }
            GlStateManager.translate(0, 0.15, 0);
            GlStateManager.scale(0.7, 0.7, 0.7);
            TESRCollectorCrystal.renderCrystal(null, true, true);
            GlStateManager.popMatrix();
            TextureHelper.refreshTextureBindState();
        }

        AddedActiveCraftingTask task = te.getAddedActiveCraftingTask();
        if(task != null) {
            task.getRecipeToCraft().onCraftTESRRender(te, x, y, z, partialTicks);
        }
    }

}
