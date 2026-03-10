package com.wdcftgg.astralaltar.integration.jei;

import com.google.common.collect.Maps;
import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.Point;
import java.util.Map;

public class GodAltarRecipeCategory implements IRecipeCategory<GodAltarRecipeWrapper> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(AstralAltar.MODID, "textures/gui/jei/recipealtargod.png");
    private static final int BACKGROUND_WIDTH = 154;
    private static final int BACKGROUND_HEIGHT = 200;
    private static final int FOCUS_SLOT_ID = 25;
    private static final Map<Integer, Point> SLOT_POSITIONS = Maps.newHashMap();

    static {
        int xGodLeft = 11;
        int xLeft = 30;
        int xInnerLeft = 49;
        int xCenter = 68;
        int xInnerRight = 87;
        int xRight = 106;
        int xGodRight = 125;

        int yFocus = 20;
        int yGodTop = 57;
        int yUpper = 76;
        int yInnerUpper = 95;
        int yCenter = 114;
        int yInnerLower = 133;
        int yLower = 152;
        int yGodBottom = 171;

        put(ShapedRecipeSlot.UPPER_LEFT, xInnerLeft, yInnerUpper);
        put(ShapedRecipeSlot.UPPER_CENTER, xCenter, yInnerUpper);
        put(ShapedRecipeSlot.UPPER_RIGHT, xInnerRight, yInnerUpper);
        put(ShapedRecipeSlot.LEFT, xInnerLeft, yCenter);
        put(ShapedRecipeSlot.CENTER, xCenter, yCenter);
        put(ShapedRecipeSlot.RIGHT, xInnerRight, yCenter);
        put(ShapedRecipeSlot.LOWER_LEFT, xInnerLeft, yInnerLower);
        put(ShapedRecipeSlot.LOWER_CENTER, xCenter, yInnerLower);
        put(ShapedRecipeSlot.LOWER_RIGHT, xInnerRight, yInnerLower);

        put(AttunementRecipe.AttunementAltarSlot.UPPER_LEFT, xLeft, yUpper);
        put(AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT, xRight, yUpper);
        put(AttunementRecipe.AttunementAltarSlot.LOWER_LEFT, xLeft, yLower);
        put(AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT, xRight, yLower);

        put(ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT, xInnerLeft, yUpper);
        put(ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT, xInnerRight, yUpper);
        put(ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT, xLeft, yInnerUpper);
        put(ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT, xRight, yInnerUpper);
        put(ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT, xLeft, yInnerLower);
        put(ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT, xRight, yInnerLower);
        put(ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT, xInnerLeft, yLower);
        put(ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT, xInnerRight, yLower);

        put(TraitRecipe.TraitRecipeSlot.UPPER_CENTER, xCenter, yUpper);
        put(TraitRecipe.TraitRecipeSlot.LEFT_CENTER, xLeft, yCenter);
        put(TraitRecipe.TraitRecipeSlot.RIGHT_CENTER, xRight, yCenter);
        put(TraitRecipe.TraitRecipeSlot.LOWER_CENTER, xCenter, yLower);

        put(FOCUS_SLOT_ID, xCenter, yFocus);

        put(GodRecipe.GodRecipeSlot.UPPER_LEFT, xInnerLeft, yGodTop);
        put(GodRecipe.GodRecipeSlot.UPPER_CENTER, xCenter, yGodTop);
        put(GodRecipe.GodRecipeSlot.UPPER_RIGHT, xInnerRight, yGodTop);
        put(GodRecipe.GodRecipeSlot.LOWER_LEFT, xInnerLeft, yGodBottom);
        put(GodRecipe.GodRecipeSlot.LOWER_CENTER, xCenter, yGodBottom);
        put(GodRecipe.GodRecipeSlot.LOWER_RIGHT, xInnerRight, yGodBottom);
        put(GodRecipe.GodRecipeSlot.LEFT_UPPER, xGodLeft, yInnerUpper);
        put(GodRecipe.GodRecipeSlot.LEFT_CENTER, xGodLeft, yCenter);
        put(GodRecipe.GodRecipeSlot.LEFT_LOWER, xGodLeft, yInnerLower);
        put(GodRecipe.GodRecipeSlot.RIGHT_UPPER, xGodRight, yInnerUpper);
        put(GodRecipe.GodRecipeSlot.RIGHT_CENTER, xGodRight, yCenter);
        put(GodRecipe.GodRecipeSlot.RIGHT_LOWER, xGodRight, yInnerLower);
        put(GodRecipe.GodRecipeSlot.UPPER_LEFT_CORNER, xGodLeft, yGodTop);
        put(GodRecipe.GodRecipeSlot.UPPER_RIGHT_CORNER, xGodRight, yGodTop);
        put(GodRecipe.GodRecipeSlot.LOWER_LEFT_CORNER, xGodLeft, yGodBottom);
        put(GodRecipe.GodRecipeSlot.LOWER_RIGHT_CORNER, xGodRight, yGodBottom);
    }

    private static void put(ShapedRecipeSlot slot, int x, int y) {
        put(slot.getSlotID(), x, y);
    }

    private static void put(AttunementRecipe.AttunementAltarSlot slot, int x, int y) {
        put(slot.getSlotId(), x, y);
    }

    private static void put(ConstellationRecipe.ConstellationAtlarSlot slot, int x, int y) {
        put(slot.getSlotId(), x, y);
    }

    private static void put(TraitRecipe.TraitRecipeSlot slot, int x, int y) {
        put(slot.getSlotId(), x, y);
    }

    private static void put(GodRecipe.GodRecipeSlot slot, int x, int y) {
        put(slot.getSlotId(), x, y);
    }

    private static void put(int slotId, int x, int y) {
        SLOT_POSITIONS.put(slotId, new Point(x, y));
    }

    private final IDrawable background;

    public GodAltarRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT).setTextureSize(BACKGROUND_WIDTH, BACKGROUND_HEIGHT).build();
    }

    @Override
    public String getUid() {
        return AstralAltarJeiPlugin.ID_ALTAR_GOD;
    }

    @Override
    public String getTitle() {
        return I18n.format("tile.astralaltar.god_altar.name");
    }

    @Override
    public String getModName() {
        return AstralAltar.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    public Class<GodAltarRecipeWrapper> getRecipeClass() {
        return GodAltarRecipeWrapper.class;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GodAltarRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(0, false, 70, 17);

        int index = 1;
        index = initShaped(stacks, index);
        index = initAttunement(stacks, index);
        index = initConstellation(stacks, index);
        index = initTrait(stacks, index);
        index = initGod(stacks, index);
        initOuterItems(stacks, index, recipeWrapper.getRecipe().getGodItemHandles().size());

        stacks.set(ingredients);
        stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (!input && Minecraft.getMinecraft().gameSettings.showDebugInfo && recipeWrapper.getRecipe().getNativeRecipe().getRegistryName() != null) {
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + recipeWrapper.getRecipe().getNativeRecipe().getRegistryName().toString());
            }
        });
    }

    private int initShaped(IGuiItemStackGroup stacks, int startIndex) {
        int index = startIndex;
        for (ShapedRecipeSlot slot : ShapedRecipeSlot.values()) {
            Point point = SLOT_POSITIONS.get(slot.getSlotID());
            stacks.init(index++, true, point.x, point.y);
        }
        return index;
    }

    private int initAttunement(IGuiItemStackGroup stacks, int startIndex) {
        int index = startIndex;
        for (AttunementRecipe.AttunementAltarSlot slot : AttunementRecipe.AttunementAltarSlot.values()) {
            Point point = SLOT_POSITIONS.get(slot.getSlotId());
            stacks.init(index++, true, point.x, point.y);
        }
        return index;
    }

    private int initConstellation(IGuiItemStackGroup stacks, int startIndex) {
        int index = startIndex;
        for (ConstellationRecipe.ConstellationAtlarSlot slot : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            Point point = SLOT_POSITIONS.get(slot.getSlotId());
            stacks.init(index++, true, point.x, point.y);
        }
        return index;
    }

    private int initTrait(IGuiItemStackGroup stacks, int startIndex) {
        int index = startIndex;
        for (TraitRecipe.TraitRecipeSlot slot : TraitRecipe.TraitRecipeSlot.values()) {
            Point point = SLOT_POSITIONS.get(slot.getSlotId());
            stacks.init(index++, true, point.x, point.y);
        }
        return index;
    }

    private int initGod(IGuiItemStackGroup stacks, int startIndex) {
        int index = startIndex;
        for (GodRecipe.GodRecipeSlot slot : GodRecipe.GodRecipeSlot.values()) {
            Point point = SLOT_POSITIONS.get(slot.getSlotId());
            stacks.init(index++, true, point.x, point.y);
        }
        return index;
    }

    private void initOuterItems(IGuiItemStackGroup stacks, int startIndex, int count) {
        if (count <= 0) {
            return;
        }
        int centerX = 68;
        int centerY = 114;
        double radius = 80D;

        for(int i = 0; i < count; ++i) {
            double part = (double)i / (double)count * (double)2.0F * Math.PI;
            part = MathHelper.clamp(part, (double)0.0F, (Math.PI * 2D));
            part += Math.PI;
            double xAdd = Math.sin(part) * radius;
            double yAdd = Math.cos(part) * radius;
            stacks.init(startIndex + i, true, MathHelper.floor((double)centerX + xAdd), MathHelper.floor((double)centerY + yAdd));
        }
    }
}
