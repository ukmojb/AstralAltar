package com.wdcftgg.astralaltar.integration.jei;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.crafting.altar.GodRecipe;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class GodAltarRecipeWrapper extends JEIBaseWrapper {

    private static final int OUTER_CONSTELLATION_SIZE = 40;

    private static final int[][] OUTER_CONSTELLATION_POSITIONS = new int[][] {
            {0, 26},
            {120, 26},
            {120, 178},
            {0, 178}
    };

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

            ingredients.setInputLists(ItemStack.class, inputs);
            ingredients.setOutput(ItemStack.class, recipe.getOutputForRender());
        } finally {
            ItemHandle.ignoreGatingRequirement = prevIgnoreGating;
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

        if (this.recipe.getRequiredConstellation() != null) {
            GlStateManager.disableAlpha();
            RenderConstellation.renderConstellationIntoGUI(this.recipe.getRequiredConstellation().getConstellationColor(), this.recipe.getRequiredConstellation(), 0, 40, 0.0F, recipeWidth, recipeHeight - 40, (double)2.0F, new RenderConstellation.BrightnessFunction() {
                public float getBrightness() {
                    return 0.5F;
                }
            }, true, false);
            GlStateManager.enableAlpha();
        }

        List<IConstellation> outerConstellations = this.recipe.getRequiredOuterConstellations();
        if (!outerConstellations.isEmpty()) {
            GlStateManager.disableAlpha();
            for (int i = 0; i < outerConstellations.size() && i < OUTER_CONSTELLATION_POSITIONS.length; i++) {
                IConstellation outer = outerConstellations.get(i);
                int[] pos = OUTER_CONSTELLATION_POSITIONS[i];
                RenderConstellation.renderConstellationIntoGUI(
                        outer.getConstellationColor(),
                        outer,
                        pos[0],
                        pos[1],
                        0.0F,
                        OUTER_CONSTELLATION_SIZE,
                        OUTER_CONSTELLATION_SIZE,
                        (double) 1.15F,
                        new RenderConstellation.BrightnessFunction() {
                            @Override
                            public float getBrightness() {
                                return 0.5F;
                            }
                        },
                        true,
                        false
                );
            }
            GlStateManager.enableAlpha();
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (!GuiScreen.isShiftKeyDown()) {
            return Lists.newArrayList();
        }

        if (this.recipe.getLiquidStarlightRequired() > 0
                && GodAltarRecipeCategory.isMouseOverRenderedGodLiquidArea(Minecraft.getMinecraft(), this.recipe, mouseX, mouseY)) {
            return Lists.newArrayList("需要星能液: " + this.recipe.getLiquidStarlightRequired() + " mB");
        }

        List<IConstellation> outerConstellations = this.recipe.getRequiredOuterConstellations();
        for (int i = 0; i < outerConstellations.size() && i < OUTER_CONSTELLATION_POSITIONS.length; i++) {
            int[] pos = OUTER_CONSTELLATION_POSITIONS[i];
            if (mouseX >= pos[0] && mouseX < pos[0] + OUTER_CONSTELLATION_SIZE
                    && mouseY >= pos[1] && mouseY < pos[1] + OUTER_CONSTELLATION_SIZE) {
                IConstellation constellation = outerConstellations.get(i);
                String key = constellation.getUnlocalizedName();
                return Lists.newArrayList(I18n.hasKey(key) ? I18n.format(key) : key);
            }
        }
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
