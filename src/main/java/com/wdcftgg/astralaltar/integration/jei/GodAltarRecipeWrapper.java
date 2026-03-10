package com.wdcftgg.astralaltar.integration.jei;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class GodAltarRecipeWrapper implements IRecipeWrapper {

    private final GodRecipe recipe;

    public GodAltarRecipeWrapper(GodRecipe recipe) {
        this.recipe = recipe;
    }

    public GodRecipe getRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        AccessibleRecipe nativeRecipe = recipe.getNativeRecipe();
        boolean prevIgnoreGating = ItemHandle.ignoreGatingRequirement;
        ItemHandle.ignoreGatingRequirement = true;
        try {
            List<List<ItemStack>> inputs = new ArrayList<>();

            for (ShapedRecipeSlot slot : ShapedRecipeSlot.values()) {
                NonNullList<ItemStack> stacks = nativeRecipe.getExpectedStackForRender(slot);
                inputs.add(stacks == null ? Lists.newArrayList() : Lists.newArrayList(stacks));
            }
            for (AttunementRecipe.AttunementAltarSlot slot : AttunementRecipe.AttunementAltarSlot.values()) {
                inputs.add(recipe.getAttItems(slot));
            }
            for (ConstellationRecipe.ConstellationAtlarSlot slot : ConstellationRecipe.ConstellationAtlarSlot.values()) {
                inputs.add(recipe.getCstItems(slot));
            }
            for (TraitRecipe.TraitRecipeSlot slot : TraitRecipe.TraitRecipeSlot.values()) {
                inputs.add(recipe.getInnerTraitItems(slot));
            }
            for (GodRecipe.GodRecipeSlot slot : GodRecipe.GodRecipeSlot.values()) {
                inputs.add(recipe.getGodItems(slot));
            }
            inputs.addAll(recipe.getGodItems());

            ingredients.setInputLists(ItemStack.class, inputs);
            ingredients.setOutput(ItemStack.class, recipe.getOutputForRender());
        } finally {
            ItemHandle.ignoreGatingRequirement = prevIgnoreGating;
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
//        IConstellation requiredConstellation = recipe.getRequiredConstellation();
//        if (requiredConstellation != null) {
//            GlStateManager.disableAlpha();
//            RenderConstellation.renderConstellationIntoGUI(
//                    requiredConstellation.getConstellationColor(),
//                    requiredConstellation,
//                    4,
//                    50,
//                    0F,
//                    30,
//                    30,
//                    1.6D,
//                    new RenderConstellation.BrightnessFunction() {
//                        @Override
//                        public float getBrightness() {
//                            return 1F;
//                        }
//                    },
//                    true,
//                    false
//            );
//            GlStateManager.enableAlpha();
//        }

        if (this.recipe.getRequiredConstellation() != null) {
            GlStateManager.disableAlpha();
            RenderConstellation.renderConstellationIntoGUI(this.recipe.getRequiredConstellation().getConstellationColor(), this.recipe.getRequiredConstellation(), 0, 40, 0.0F, recipeWidth, recipeHeight - 40, (double)2.0F, new RenderConstellation.BrightnessFunction() {
                public float getBrightness() {
                    return 0.5F;
                }
            }, true, false);
            GlStateManager.enableAlpha();
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
