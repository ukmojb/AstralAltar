package com.wdcftgg.astralaltar.integration.jei;

import com.google.common.collect.Maps;
import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.crafting.altar.GodRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GodAltarRecipeCategory extends JEIBaseCategory<GodAltarRecipeWrapper> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(AstralAltar.MODID, "textures/gui/jei/recipealtargod.png");
    private static final ResourceLocation GOD_LIQUID_STARLIGHT_MASK = new ResourceLocation(AstralAltar.MODID, "textures/gui/jei/godliquidstarlight.png");
    private static final ResourceLocation STARLIGHT_LIQUID_FLOW = new ResourceLocation("astralsorcery", "textures/blocks/fluid/starlight_flow.png");
    private static final int BACKGROUND_WIDTH = 154;
    private static final int BACKGROUND_HEIGHT = 216;
    static final int GOD_LIQUID_STARLIGHT_X = 52;
    static final int GOD_LIQUID_STARLIGHT_Y = 0;
    static final int GOD_LIQUID_STARLIGHT_TOTAL = 10000;
    private static final float FULL_CIRCLE_DEGREES = 360F;
    private static final float DEFAULT_START_ANGLE = 320.71F;
    private static final long FLOW_FRAME_MILLIS = 100L;
    private static final int FOCUS_SLOT_ID = 25;
    private static final Map<Integer, Point> SLOT_POSITIONS = Maps.newHashMap();
    @Nullable
    private static MaskTextureData sharedGodLiquidMaskData;

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
    @Nullable
    private GodAltarRecipeWrapper currentRecipeWrapper;
    @Nullable
    private MaskTextureData godLiquidMaskData;
    @Nullable
    private FlowTextureData godLiquidFlowData;
    @Nullable
    private DynamicTexture godLiquidDynamicTexture;
    @Nullable
    private ResourceLocation godLiquidDynamicTextureLocation;
    private int lastRenderedGodLiquidRequirement = Integer.MIN_VALUE;
    private int lastRenderedFlowFrame = Integer.MIN_VALUE;

    public GodAltarRecipeCategory(IGuiHelper guiHelper) {
        super("jei.category.altar.god", AstralAltarJeiPlugin.ID_ALTAR_GOD);
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
        drawGodLiquidStarlightRequirement(minecraft);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GodAltarRecipeWrapper recipeWrapper, IIngredients ingredients) {
        this.currentRecipeWrapper = recipeWrapper;
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(100, false, 70, 17);

        int index = 0;
        index = initShaped(stacks, index);
        index = initAttunement(stacks, index);
        index = initConstellation(stacks, index);
        index = initTrait(stacks, index);
        index = initGod(stacks, index);
        List<Integer> outerSlots = initOuterItems(stacks, index, recipeWrapper.getRecipe().getGodItemHandles().size());

        stacks.set(ingredients);
        List<NonNullList<ItemStack>> outerItems = recipeWrapper.getRecipe().getGodItems();
        for (int i = 0; i < outerSlots.size() && i < outerItems.size(); i++) {
            stacks.set(outerSlots.get(i), outerItems.get(i));
        }
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

    private List<Integer> initOuterItems(IGuiItemStackGroup stacks, int startIndex, int count) {
        List<Integer> initializedSlots = new java.util.ArrayList<>();
        if (count <= 0) {
            return initializedSlots;
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
            int slotIndex = startIndex + i;
            stacks.init(slotIndex, false, MathHelper.floor((double)centerX + xAdd), MathHelper.floor((double)centerY + yAdd));
            initializedSlots.add(slotIndex);
        }
        return initializedSlots;
    }

    private void drawGodLiquidStarlightRequirement(Minecraft minecraft) {
        if (currentRecipeWrapper == null) {
            return;
        }

        int required = MathHelper.clamp(currentRecipeWrapper.getRecipe().getLiquidStarlightRequired(), 0, GOD_LIQUID_STARLIGHT_TOTAL);
        if (required <= 0) {
            return;
        }

        MaskTextureData maskData = getGodLiquidMaskData(minecraft);
        if (maskData == null) {
            return;
        }

        ensureGodLiquidTexture(minecraft, maskData, required);
        if (godLiquidDynamicTextureLocation == null) {
            return;
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        minecraft.getTextureManager().bindTexture(godLiquidDynamicTextureLocation);
        Gui.drawModalRectWithCustomSizedTexture(
                GOD_LIQUID_STARLIGHT_X,
                GOD_LIQUID_STARLIGHT_Y,
                0F,
                0F,
                maskData.width,
                maskData.height,
                maskData.width,
                maskData.height
        );
    }

    private void ensureGodLiquidTexture(Minecraft minecraft, MaskTextureData maskData, int required) {
        FlowTextureData flowData = getGodLiquidFlowData(minecraft);
        if (flowData == null) {
            return;
        }

        if (godLiquidDynamicTexture == null) {
            godLiquidDynamicTexture = new DynamicTexture(maskData.width, maskData.height);
            godLiquidDynamicTextureLocation = minecraft.getTextureManager().getDynamicTextureLocation("astralaltar_jei_god_liquid_starlight", godLiquidDynamicTexture);
            lastRenderedGodLiquidRequirement = Integer.MIN_VALUE;
            lastRenderedFlowFrame = Integer.MIN_VALUE;
        }

        int currentFrame = getCurrentFlowFrame(flowData);
        if (lastRenderedGodLiquidRequirement == required && lastRenderedFlowFrame == currentFrame) {
            return;
        }

        updateGodLiquidTexture(maskData, flowData, currentFrame, required, godLiquidDynamicTexture.getTextureData());
        godLiquidDynamicTexture.updateDynamicTexture();
        lastRenderedGodLiquidRequirement = required;
        lastRenderedFlowFrame = currentFrame;
    }

    private void updateGodLiquidTexture(MaskTextureData maskData, FlowTextureData flowData, int frameIndex, int required, int[] outputPixels) {
        Arrays.fill(outputPixels, 0);

        boolean renderFullCircle = required >= GOD_LIQUID_STARLIGHT_TOTAL;
        float visibleSweep = MathHelper.clamp((float) required / (float) GOD_LIQUID_STARLIGHT_TOTAL, 0F, 1F) * FULL_CIRCLE_DEGREES;

        for (int y = 0; y < maskData.height; y++) {
            for (int x = 0; x < maskData.width; x++) {
                int index = x + y * maskData.width;
                if (!maskData.opaqueMask[index]) {
                    continue;
                }

                float clockwiseDistance = getClockwiseDistance(maskData.startAngleDegrees, maskData.clockwiseAngles[index]);
                if (!renderFullCircle && clockwiseDistance > visibleSweep) {
                    continue;
                }

                int flowColor = sampleFlowPixel(flowData, frameIndex, x, y);
                int flowAlpha = flowColor >> 24 & 255;
                if (flowAlpha <= 0) {
                    continue;
                }

                outputPixels[index] = flowColor;
            }
        }
    }

    private int sampleFlowPixel(FlowTextureData flowData, int frameIndex, int x, int y) {
        int wrappedX = Math.floorMod(x, flowData.frameWidth);
        int wrappedY = Math.floorMod(y, flowData.frameHeight);
        int sampleY = wrappedY + frameIndex * flowData.frameHeight;
        return flowData.pixels[wrappedX + sampleY * flowData.width];
    }

    private int getCurrentFlowFrame(FlowTextureData flowData) {
        if (flowData.frameCount <= 1) {
            return 0;
        }

        long elapsedTicks = Math.max(0L, Minecraft.getSystemTime() / FLOW_FRAME_MILLIS);
        long animationTick = flowData.totalAnimationTicks > 0 ? elapsedTicks % flowData.totalAnimationTicks : 0L;
        int accumulatedTicks = 0;
        for (int i = 0; i < flowData.frameCount; i++) {
            accumulatedTicks += flowData.frameTimes[i];
            if (animationTick < accumulatedTicks) {
                return i;
            }
        }
        return flowData.frameCount - 1;
    }

    @Nullable
    private MaskTextureData getGodLiquidMaskData(Minecraft minecraft) {
        godLiquidMaskData = getSharedGodLiquidMaskData(minecraft);
        return godLiquidMaskData;
    }

    @Nullable
    private FlowTextureData getGodLiquidFlowData(Minecraft minecraft) {
        if (godLiquidFlowData != null) {
            return godLiquidFlowData;
        }

        try (IResource resource = minecraft.getResourceManager().getResource(STARLIGHT_LIQUID_FLOW)) {
            BufferedImage image = TextureUtil.readBufferedImage(resource.getInputStream());
            AnimationMetadataSection animation = resource.getMetadata("animation");
            godLiquidFlowData = FlowTextureData.load(image, animation);
            return godLiquidFlowData;
        } catch (IOException e) {
            AstralAltar.LogWarning("Failed to load JEI god liquid starlight flow texture: %s", e.getMessage());
            return null;
        }
    }

    public static boolean isMouseOverRenderedGodLiquidArea(Minecraft minecraft, GodRecipe recipe, int mouseX, int mouseY) {
        int required = MathHelper.clamp(recipe.getLiquidStarlightRequired(), 0, GOD_LIQUID_STARLIGHT_TOTAL);
        if (required <= 0) {
            return false;
        }

        MaskTextureData maskData = getSharedGodLiquidMaskData(minecraft);
        if (maskData == null) {
            return false;
        }

        int localX = mouseX - GOD_LIQUID_STARLIGHT_X;
        int localY = mouseY - GOD_LIQUID_STARLIGHT_Y;
        if (localX < 0 || localY < 0 || localX >= maskData.width || localY >= maskData.height) {
            return false;
        }

        int index = localX + localY * maskData.width;
        if (!maskData.opaqueMask[index]) {
            return false;
        }

        if (required >= GOD_LIQUID_STARLIGHT_TOTAL) {
            return true;
        }

        float visibleSweep = MathHelper.clamp((float) required / (float) GOD_LIQUID_STARLIGHT_TOTAL, 0F, 1F) * FULL_CIRCLE_DEGREES;
        return getClockwiseDistance(maskData.startAngleDegrees, maskData.clockwiseAngles[index]) <= visibleSweep;
    }

    @Nullable
    private static MaskTextureData getSharedGodLiquidMaskData(Minecraft minecraft) {
        if (sharedGodLiquidMaskData != null) {
            return sharedGodLiquidMaskData;
        }

        try (IResource resource = minecraft.getResourceManager().getResource(GOD_LIQUID_STARLIGHT_MASK)) {
            BufferedImage image = TextureUtil.readBufferedImage(resource.getInputStream());
            sharedGodLiquidMaskData = MaskTextureData.load(image);
            return sharedGodLiquidMaskData;
        } catch (IOException e) {
            AstralAltar.LogWarning("Failed to load JEI god liquid starlight mask: %s", e.getMessage());
            return null;
        }
    }

    private static float getClockwiseDistance(float startAngleDegrees, float angleDegrees) {
        float delta = angleDegrees - startAngleDegrees;
        if (delta < 0F) {
            delta += FULL_CIRCLE_DEGREES;
        }
        return delta;
    }

    private static final class MaskTextureData {

        private final int width;
        private final int height;
        private final boolean[] opaqueMask;
        private final float[] clockwiseAngles;
        private final float startAngleDegrees;

        private MaskTextureData(int width, int height, boolean[] opaqueMask, float[] clockwiseAngles, float startAngleDegrees) {
            this.width = width;
            this.height = height;
            this.opaqueMask = opaqueMask;
            this.clockwiseAngles = clockwiseAngles;
            this.startAngleDegrees = startAngleDegrees;
        }

        private static MaskTextureData load(BufferedImage image) {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            boolean[] opaqueMask = new boolean[pixels.length];
            float[] clockwiseAngles = new float[pixels.length];
            float startAngle = Float.POSITIVE_INFINITY;
            float centerX = width / 2F;
            float centerY = height / 2F;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = x + y * width;
                    int color = pixels[index];
                    int alpha = color >> 24 & 255;
                    if (alpha <= 0) {
                        continue;
                    }
                    opaqueMask[index] = true;

                    float dx = (x + 0.5F) - centerX;
                    float dy = (y + 0.5F) - centerY;
                    float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                    if (angle < 0F) {
                        angle += FULL_CIRCLE_DEGREES;
                    }
                    clockwiseAngles[index] = angle;

                    // Match the requested start point: the left edge of the upper-right shadow/lobe.
                    if (dx > 0F && dy < 0F && dx >= -dy) {
                        startAngle = Math.min(startAngle, angle);
                    }
                }
            }

            if (!Float.isFinite(startAngle)) {
                startAngle = DEFAULT_START_ANGLE;
            }
            return new MaskTextureData(width, height, opaqueMask, clockwiseAngles, startAngle);
        }
    }

    private static final class FlowTextureData {

        private final int width;
        private final int[] pixels;
        private final int frameWidth;
        private final int frameHeight;
        private final int frameCount;
        private final int[] frameTimes;
        private final int totalAnimationTicks;

        private FlowTextureData(int width, int[] pixels, int frameWidth, int frameHeight, int frameCount, int[] frameTimes, int totalAnimationTicks) {
            this.width = width;
            this.pixels = pixels;
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.frameCount = frameCount;
            this.frameTimes = frameTimes;
            this.totalAnimationTicks = totalAnimationTicks;
        }

        private static FlowTextureData load(BufferedImage image, @Nullable AnimationMetadataSection animation) {
            int width = image.getWidth();
            int height = image.getHeight();
            int frameWidth = width;
            int frameHeight = width > 0 ? width : height;
            int inferredFrameCount = Math.max(1, height / Math.max(1, frameHeight));
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            List<Integer> frameIndexList = new ArrayList<>();
            List<Integer> frameTimeList = new ArrayList<>();

            if (animation != null && animation.getFrameCount() > 0) {
                for (int i = 0; i < animation.getFrameCount(); i++) {
                    int frameIndex = MathHelper.clamp(animation.getFrameIndex(i), 0, inferredFrameCount - 1);
                    int frameTime = Math.max(1, animation.getFrameTimeSingle(i));
                    frameIndexList.add(frameIndex);
                    frameTimeList.add(frameTime);
                }
            } else {
                int defaultFrameTime = animation != null ? Math.max(1, animation.getFrameTime()) : 1;
                for (int i = 0; i < inferredFrameCount; i++) {
                    frameIndexList.add(i);
                    frameTimeList.add(defaultFrameTime);
                }
            }

            int frameCount = frameIndexList.size();
            int[] frameTimes = new int[frameCount];
            int totalAnimationTicks = 0;
            for (int i = 0; i < frameCount; i++) {
                frameTimes[i] = frameTimeList.get(i);
                totalAnimationTicks += frameTimes[i];
            }

            // Reorder pixels virtually through frame indices so sampling follows the metadata sequence.
            if (!frameIndexList.isEmpty()) {
                int[] sequencedPixels = new int[width * frameHeight * frameCount];
                for (int i = 0; i < frameCount; i++) {
                    int sourceFrameIndex = frameIndexList.get(i);
                    int sourceOffset = sourceFrameIndex * frameHeight * width;
                    int targetOffset = i * frameHeight * width;
                    System.arraycopy(pixels, sourceOffset, sequencedPixels, targetOffset, frameHeight * width);
                }
                pixels = sequencedPixels;
            }

            return new FlowTextureData(width, pixels, frameWidth, frameHeight, frameCount, frameTimes, totalAnimationTicks);
        }
    }
}
