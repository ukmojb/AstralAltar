package com.wdcftgg.astralaltar.crafting.recipe;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.util.AARenderConstellation;
import com.wdcftgg.astralaltar.crafting.AddedAbstractAltarRecipe;
import com.wdcftgg.astralaltar.crafting.AddedActiveCraftingTask;
import com.wdcftgg.astralaltar.crafting.IAddedCraftingProgress;
import com.wdcftgg.astralaltar.init.multiblock.MultiblockContainmentChalice;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe.AttunementAltarSlot;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe.ConstellationAtlarSlot;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe.TraitRecipeSlot;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import com.wdcftgg.astralaltar.cilent.render.BakedQuadRetextured;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.init.Biomes;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.BufferUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GodRecipe extends AddedAbstractAltarRecipe implements IAddedCraftingProgress {

    private static final ResourceLocation starlightLiquidStill = new ResourceLocation("astralsorcery:blocks/fluid/starlight_still");
    private static final String activeTAG = "isActive";
    private static final BlockPos[] outerCrystalOffsets = new BlockPos[] {
            new BlockPos(-3, 6, -3),
            new BlockPos(-3, 6, 3),
            new BlockPos(3, 6, -3),
            new BlockPos(3, 6, 3)
    };
    public static final int constellationBegin = 500;
    public static final int constellationEnd = 600;
    private static final int liquidStarlightBegin = 770;
    private static final int liquidStarlightEnd = 1070;
    private static final int liquidGuideDelayTicks = 30;
    private static final float liquidGuideFadeInStep = 0.035F;
    private static final float liquidGuideFadeOutStep = 0.06F;
    private static final float liquidGuideMaxAlpha = 1.00F;
    private static final int chaliceScanRangeXZ = 20;
    private static final int chaliceScanRangeY = 10;
    private static final int chaliceRescanIntervalTicks = 20;
    private static final int chaliceScanBudgetPerTick = 320;
    private static final float risingClipIncreasePerTick = 0.01F;

    @SideOnly(Side.CLIENT)
    private static final PatternBlockArray liquidGuidePattern = new MultiblockContainmentChalice();
    @SideOnly(Side.CLIENT)
    private static final IBlockAccess liquidGuideAirWorld = new WorldBlockArrayRenderAccess() {
        @Override
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 0x00F000F0;
        }
    };
    @SideOnly(Side.CLIENT)
    private static LiquidGuidePatternMetrics liquidGuidePatternMetrics;

    @SideOnly(Side.CLIENT)
    private final Map<String, OuterConstellationRenderState> outerConstellationRenderStates = new HashMap<>();
    @SideOnly(Side.CLIENT)
    private final Map<String, OuterLiquidGuideRenderState> outerLiquidGuideRenderStates = new HashMap<>();
    @SideOnly(Side.CLIENT)
    private final Map<String, RisingClipRenderState> risingClipRenderStates = new HashMap<>();
    private final Map<String, ChaliceSearchCache> chaliceSearchCaches = new HashMap<>();



    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();

    private Map<GodRecipeSlot, ItemHandle> matchGodStacks = new HashMap<>();

    private IConstellation requiredConstellation = null;

    private List<IConstellation> requiredOuterConstellations = new ArrayList<>(4);

    private Map<TraitRecipeSlot, ItemHandle> matchTraitStacks = new HashMap<>();

    private int requiredLiquidStarlight = 0;
    private Map<ConstellationAtlarSlot, ItemHandle> matchStacks = new HashMap<>();
    private Map<AttunementAltarSlot, ItemHandle> additionalSlots = new HashMap<>();

    protected GodRecipe(TileGodAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public GodRecipe(AccessibleRecipe recipe) {
        super(TileGodAltar.AltarLevel.GOD_CRAFT, recipe);
        setPassiveStarlightRequirement(7500);
    }

    public GodRecipe setAttItem(Block b, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemStack(b), slots);
    }

    public GodRecipe setAttItem(Item i, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemStack(i), slots);
    }

    public GodRecipe setAttItem(ItemStack stack, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(stack), slots);
    }

    public GodRecipe setAttItem(String oreDict, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(oreDict), slots);
    }

    public GodRecipe setAttItem(FluidStack fluid, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(fluid), slots);
    }

    public GodRecipe setAttItem(Fluid fluid, int mbAmount, AttunementAltarSlot... slots) {
        return setAttItem(new FluidStack(fluid, mbAmount), slots);
    }

    public GodRecipe setAttItem(Fluid fluid, AttunementAltarSlot... slots) {
        return setAttItem(fluid, 1000, slots);
    }

    public GodRecipe setAttItem(ItemHandle handle, AttunementAltarSlot... slots) {
        for (AttunementAltarSlot slot : slots) {
            additionalSlots.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getAttItems(AttunementAltarSlot slot) {
        ItemHandle handle = additionalSlots.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getAttItemHandle(AttunementAltarSlot slot) {
        return additionalSlots.get(slot);
    }

    public GodRecipe setCstItem(Item i, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemStack(i), slots);
    }

    public GodRecipe setCstItem(Block b, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemStack(b), slots);
    }

    public GodRecipe setCstItem(ItemStack stack, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(stack), slots);
    }

    public GodRecipe setCstItem(String oreDict, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(oreDict), slots);
    }

    public GodRecipe setCstItem(FluidStack fluid, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(fluid), slots);
    }

    public GodRecipe setCstItem(Fluid fluid, int mbAmount, ConstellationAtlarSlot... slots) {
        return setCstItem(new FluidStack(fluid, mbAmount), slots);
    }

    public GodRecipe setCstItem(Fluid fluid, ConstellationAtlarSlot... slots) {
        return setCstItem(fluid, 1000, slots);
    }

    public GodRecipe setCstItem(ItemHandle handle, ConstellationAtlarSlot... slots) {
        for (ConstellationAtlarSlot slot : slots) {
            matchStacks.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getCstItems(ConstellationAtlarSlot slot) {
        ItemHandle handle = matchStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getCstItemHandle(ConstellationAtlarSlot slot) {
        return matchStacks.get(slot);
    }

    public GodRecipe setInnerTraitItem(Item i, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemStack(i), slots);
    }

    public GodRecipe setInnerTraitItem(Block b, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemStack(b), slots);
    }

    public GodRecipe setInnerTraitItem(ItemStack stack, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(stack), slots);
    }

    public GodRecipe setInnerTraitItem(String oreDict, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(oreDict), slots);
    }

    public GodRecipe setInnerTraitItem(FluidStack fluid, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(fluid), slots);
    }

    public GodRecipe setInnerTraitItem(Fluid fluid, int mbAmount, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new FluidStack(fluid, mbAmount), slots);
    }

    public GodRecipe setInnerTraitItem(Fluid fluid, TraitRecipeSlot... slots) {
        return setInnerTraitItem(fluid, 1000, slots);
    }

    public GodRecipe setInnerTraitItem(ItemHandle handle, TraitRecipeSlot... slots) {
        for (TraitRecipeSlot slot : slots) {
            matchTraitStacks.put(slot, handle);
        }
        return this;
    }

    public GodRecipe addOuterTraitItem(Item i) {
        return addOuterTraitItem(new ItemStack(i));
    }

    public GodRecipe addOuterTraitItem(Block b) {
        return addOuterTraitItem(new ItemStack(b));
    }

    public GodRecipe addOuterTraitItem(ItemStack stack) {
        return addOuterTraitItem(new ItemHandle(stack));
    }

    public GodRecipe addOuterTraitItem(String oreDict) {
        return addOuterTraitItem(new ItemHandle(oreDict));
    }

    public GodRecipe addOuterTraitItem(FluidStack fluid) {
        return addOuterTraitItem(new ItemHandle(fluid));
    }

    public GodRecipe addOuterTraitItem(Fluid fluid, int mbAmount) {
        return addOuterTraitItem(new FluidStack(fluid, mbAmount));
    }

    public GodRecipe addOuterTraitItem(Fluid fluid) {
        return addOuterTraitItem(fluid, 1000);
    }

    public GodRecipe addOuterTraitItem(ItemHandle handle) {
        additionallyRequiredStacks.add(handle);
        return this;
    }

    @Nonnull
    public List<NonNullList<ItemStack>> getTraitItems() {
        List<NonNullList<ItemStack>> out = Lists.newArrayList();
        for (ItemHandle handle : additionallyRequiredStacks) {
            out.add(handle.getApplicableItems());
        }
        return out;
    }

    @Nonnull
    public List<ItemHandle> getTraitItemHandles() {
        return Lists.newArrayList(additionallyRequiredStacks);
    }

    @Nonnull
    public List<ItemStack> getInnerTraitItems(TraitRecipeSlot slot) {
        ItemHandle handle = matchTraitStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getInnerTraitItemHandle(TraitRecipeSlot slot) {
        return matchTraitStacks.get(slot);
    }

    public GodRecipe setGodItem(Item i, GodRecipeSlot... slots) {
        return setGodItem(new ItemStack(i), slots);
    }

    public GodRecipe setGodItem(Block b, GodRecipeSlot... slots) {
        return setGodItem(new ItemStack(b), slots);
    }

    public GodRecipe setGodItem(ItemStack stack, GodRecipeSlot... slots) {
        return setGodItem(new ItemHandle(stack), slots);
    }

    public GodRecipe setGodItem(String oreDict, GodRecipeSlot... slots) {
        return setGodItem(new ItemHandle(oreDict), slots);
    }

    public GodRecipe setGodItem(FluidStack fluid, GodRecipeSlot... slots) {
        return setGodItem(new ItemHandle(fluid), slots);
    }

    public GodRecipe setGodItem(Fluid fluid, int mbAmount, GodRecipeSlot... slots) {
        return setGodItem(new FluidStack(fluid, mbAmount), slots);
    }

    public GodRecipe setGodItem(Fluid fluid, GodRecipeSlot... slots) {
        return setGodItem(fluid, 1000, slots);
    }

    public GodRecipe setGodItem(ItemHandle handle, GodRecipeSlot... slots) {
        for (GodRecipeSlot slot : slots) {
            matchGodStacks.put(slot, handle);
        }
        return this;
    }
    
    @Nonnull
    public List<NonNullList<ItemStack>> getGodItems() {
        List<NonNullList<ItemStack>> out = Lists.newArrayList();
        for (ItemHandle handle : additionallyRequiredStacks) {
            out.add(handle.getApplicableItems());
        }
        return out;
    }

    @Nonnull
    public List<ItemHandle> getGodItemHandles() {
        return Lists.newArrayList(additionallyRequiredStacks);
    }

    @Nonnull
    public List<ItemStack> getGodItems(GodRecipeSlot slot) {
        ItemHandle handle = matchGodStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getGodItemHandle(GodRecipeSlot slot) {
        return matchGodStacks.get(slot);
    }

    public GodRecipe setGodLiquidStarlight(int liquidStarlight) {
        this.requiredLiquidStarlight = liquidStarlight;
        return this;
    }

    public int getLiquidStarlightRequired() {
        return requiredLiquidStarlight;
    }

    public void setRequiredOuterConstellations(IConstellation... requiredOuter) {
        if (requiredOuter.length > 4) {
            throw new IllegalArgumentException("Cannot have more than 4 required outer constellations!");
        }
        this.requiredOuterConstellations = Arrays.asList(requiredOuter);
    }

    public void addRequiredOuterConstellation(IConstellation constellation) {
        if (requiredOuterConstellations.size() > 4) {
            throw new IllegalArgumentException("Cannot have more than 4 required outer constellations!");
        }
        this.requiredOuterConstellations.add(constellation);
    }

    public List<IConstellation> getRequiredOuterConstellations() {
        return requiredOuterConstellations;
    };

    public void setRequiredConstellation(IConstellation requiredConstellation) {
        this.requiredConstellation = requiredConstellation;
    }

    @Nullable
    public IConstellation getRequiredConstellation() {
        return requiredConstellation;
    }

    @Override
    public int craftingTickTime() {
        super.craftingTickTime();
        return 1300;
    }

    @Override
    public void handleInputConsumption(TileGodAltar ta, AddedActiveCraftingTask craftingTask, ItemStackHandler inventory) {
        super.handleInputConsumption(ta, craftingTask, inventory);

        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(i / 3, i % 3);
            if(mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, i);
            } else {
                handleItemConsumption(ta, slot);
            }
        }

        for (AttunementRecipe.AttunementAltarSlot slot : AttunementRecipe.AttunementAltarSlot.values()) {
            int slotId = slot.getSlotId();
            if(mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, slotId);
            } else {
                handleItemConsumption(ta, slot);
            }
        }

        for (ConstellationRecipe.ConstellationAtlarSlot slot : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            int slotId = slot.getSlotId();
            if(mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, slotId);
            } else {
                handleItemConsumption(ta, slot);
            }
        }

        for (TraitRecipeSlot slot : TraitRecipeSlot.values()) {
            int slotId = slot.getSlotId();
            if(mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, slotId);
            } else {
                handleItemConsumption(ta, slot);
            }
        }

        this.consumeOuterInputs(ta, craftingTask);

        for (GodRecipe.GodRecipeSlot slot : GodRecipe.GodRecipeSlot.values()) {
            int slotId = slot.getSlotId();
            if(mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, slotId);
            } else {
                handleItemConsumption(ta, slot);
            }
        }
    }

    @Override
    public boolean tryProcess(TileGodAltar altar, AddedActiveCraftingTask runningTask, NBTTagCompound craftingData, int activeCraftingTick, int totalCraftingTime) {
        if(!fulfillesStarlightRequirement(altar)) {
            return false; //Duh.
        }

        List<CraftingFocusStack> stacks = collectCurrentStacks(craftingData);
        if(!matchFocusStacks(altar, stacks)) {
            return false;
        }


        int required = additionallyRequiredStacks.size();
        int outerConstellationsRequired = this.requiredOuterConstellations.size();
        int liquidStarlightRequired = this.requiredLiquidStarlight;

        boolean noAdditionalItems = required <= 0; //No additional items, huh.
        boolean noOuterConstellations = outerConstellationsRequired <= 0; //无外部星座需求
        boolean noLiquidStarlight = liquidStarlightRequired <= 0; //无星能液需求


        // 从零开始，每350/required个tick检查一次是否有新的中继器被使用，如果被使用了就记录下来，直到所有的required都被使用否则合成时间不继续走，等所有的required都被使用了才继续检查液体星辉的输入（如果需要的话）
        // 0 - 350
        if (!noAdditionalItems) {
            int part = 350;
            int offset = 70;
            int cttPart = part / required;
            boolean[] hasAssignedRelay = new boolean[required];
            for (CraftingFocusStack stack : stacks) {
                if (stack.stackIndex != null && stack.stackIndex >= 0 && stack.stackIndex < required) {
                    hasAssignedRelay[stack.stackIndex] = true;
                }
            }
            for (int i = 0; i < required; i++) {
                int timing = (i * cttPart) + offset;
                if (activeCraftingTick >= timing) {
                    if (!hasAssignedRelay[i]) {
                        BlockPos next = findUnusedRelay(altar, stacks);
                        if (next != null) {
                            CraftingFocusStack stack = new CraftingFocusStack(i, next);
                            stacks.add(stack);
                            storeCurrentStacks(craftingData, stacks);
                        }
                        return false;
                    }
                }
            }
        }

        // 右键聚能水晶来激活它,激活后就算作外部星座
        // 500 - 600
        if (!noOuterConstellations) {

            int part = 100;
            int begin = 500;
            int cttPart = part / outerCrystalOffsets.length;
            List<TileCollectorCrystal> activeCrystals = getOuterActiveCrystals(altar);
            int activeCrystalCount = activeCrystals.size();
            for (int i = 0; i < outerCrystalOffsets.length; i++) {
                int timing = (i * cttPart) + begin;
                if (activeCraftingTick == timing && activeCrystalCount <= i) {
                    return false;
                }
                if (activeCraftingTick == 600) {
                    if (activeCrystalCount == outerCrystalOffsets.length) {
                        if (!(new HashSet<>(getCrystalConstellations(activeCrystals)).containsAll(requiredOuterConstellations))) {
                            destroyNonRequiredOuterCrystals(altar);
                            clearOuterConstellationCrystals(altar);
                            return false;
                        }
                    }
                }
            }


        }

        // 检查液体星辉输入
        // 770 - 1070
        if (!noLiquidStarlight) {
            int allTime = 300;
            int totalSteps = 30;
            int begin = 770;
            int cttPart = allTime / totalSteps;
            int perStep = (int) Math.floor(liquidStarlightRequired / (double) totalSteps);
            int totalTime = ((liquidStarlightRequired - altar.getFluidAmount()) / perStep);

            for (int i = 30 - totalTime; i < totalSteps; i++) {
                int timing = (i * cttPart) + begin;
                if (activeCraftingTick == timing) {
                    if (!findChaliceAndFluidTransfer(altar, perStep)) {
                        return false;
                    }
                }
            }

            if (altar.getFluidAmount() < this.requiredLiquidStarlight) {
                int timing = (30 * cttPart) + begin;
                if (activeCraftingTick == timing) {
                    int lastNeeded = this.requiredLiquidStarlight - altar.getFluidAmount();
                    return findChaliceAndFluidTransfer(altar, lastNeeded);
                }
            }
        }

        // 默认动画
        // 1070 - 1300

        return true;
    }

    private boolean findChaliceAndFluidTransfer(TileGodAltar altar, int needed) {
        TileChalice chalice = findChalice(altar, needed);

        if (chalice == null) return false;

        FluidStack fluidStack = new FluidStack(BlocksAS.fluidLiquidStarlight, needed);
        LiquidStarlightChaliceHandler.doFluidTransfer(chalice, altar, fluidStack.copy());
        chalice.getTank().drain(needed, true);
        return true;
    }

    private List<TileCollectorCrystal> findCrystals(TileGodAltar altar) {
        List<TileCollectorCrystal> crystals = new ArrayList<>();
        for (BlockPos offset : outerCrystalOffsets) {
            TileCollectorCrystal crystal = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(offset), TileCollectorCrystal.class, true);
            if (crystal != null) {
                crystals.add(crystal);
            }
        }

        return crystals;
    }

    private void destroyNonRequiredOuterCrystals(TileGodAltar altar) {
        World world = altar.getWorld();
        if (world.isRemote) {
            return;
        }

        Set<IConstellation> required = new HashSet<>(requiredOuterConstellations);
        for (TileCollectorCrystal crystal : getOuterActiveCrystals(altar)) {
            IConstellation crystalConstellation = getCrystalConstellation(crystal);
            if (crystalConstellation != null && required.contains(crystalConstellation)) {
                continue;
            }

            BlockPos pos = crystal.getPos();
            if (world.isBlockLoaded(pos)) {
                world.destroyBlock(pos, false);
            }
        }
    }

    public static boolean isCrystalActive(@Nullable TileCollectorCrystal crystal) {
        if (crystal == null) {
            return false;
        }

        NBTTagCompound data = crystal.getTileData();
        if (!data.hasKey(activeTAG, Constants.NBT.TAG_BYTE)) {
            data.setBoolean(activeTAG, false);
        }

        return data.getBoolean(activeTAG);
    }

    public static void setCrystalActive(@Nullable TileCollectorCrystal crystal, boolean active) {
        if (crystal == null) {
            return;
        }

        NBTTagCompound data = crystal.getTileData();

        data.setBoolean(activeTAG, active);
        crystal.markDirty();
    }

    @Nullable
    private static IConstellation getCrystalConstellation(@Nullable TileCollectorCrystal crystal) {
        return crystal == null ? null : crystal.getConstellation();
    }

    private static List<IConstellation> getCrystalConstellations(List<TileCollectorCrystal> crystals) {
        return crystals.stream().map(GodRecipe::getCrystalConstellation).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    public static TileGodAltar findOuterConstellationAltar(@Nullable TileCollectorCrystal crystal) {
        if (crystal == null) {
            return null;
        }

        for (BlockPos offset : outerCrystalOffsets) {
            TileGodAltar altar = MiscUtils.getTileAt(crystal.getWorld(), crystal.getPos().subtract(offset), TileGodAltar.class, true);
            if (altar != null) {
                return altar;
            }
        }

        return null;
    }
    
    public static boolean isCrafting(@Nullable TileGodAltar godAltar) {
        if (godAltar == null) {
            return false;
        }

        return godAltar.getAddedActiveCraftingTask() != null && !godAltar.getAddedActiveCraftingTask().isFinished();
    }

    private List<TileCollectorCrystal> getOuterActiveCrystals(TileGodAltar altar) {
        List<TileCollectorCrystal> active = new ArrayList<>();
        if (requiredOuterConstellations.isEmpty()) {
            return active;
        }

        for (TileCollectorCrystal crystal : findCrystals(altar)) {
            if (!isCrystalActive(crystal)) {
                continue;
            }

            active.add(crystal);
        }

        return active;
    }

    private void clearOuterConstellationCrystals(TileGodAltar altar) {
        for (TileCollectorCrystal crystal : findCrystals(altar)) {
            setCrystalActive(crystal, false);
        }
    }

    @Override
    public boolean tryProcess(TileGodAltar altar, NBTTagCompound craftingData, int activeCraftingTick, int totalCraftingTime) {
        return tryProcess(altar, null, craftingData, activeCraftingTick, totalCraftingTime);
    }

    //重写
    @Override
    public boolean matches(TileGodAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        for (AttunementAltarSlot slot : AttunementAltarSlot.values()) {
            ItemHandle expected = additionalSlots.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.getSlotId()).isEmpty()) return false;
            }
        }

        for (ConstellationAtlarSlot slot : ConstellationAtlarSlot.values()) {
            ItemHandle expected = matchStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.getSlotId()).isEmpty()) return false;
            }
        }
        for (TraitRecipeSlot slot : TraitRecipeSlot.values()) {
            ItemHandle expected = matchTraitStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.getSlotId()).isEmpty()) return false;
            }
        }

        IConstellation req = getRequiredConstellation();
        if(req != null) {
            IConstellation focus = altar.getFocusedConstellation();
            if(focus == null) return false;
            if(!req.equals(focus)) return false;
        }
        for (GodRecipeSlot slot : GodRecipeSlot.values()) {
            ItemHandle expected = matchGodStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.getSlotId()).isEmpty()) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    public void consumeOuterInputs(TileGodAltar altar, AddedActiveCraftingTask craftingTask) {
        List<CraftingFocusStack> stacks = collectCurrentStacks(craftingTask.getCraftingData());
        for (CraftingFocusStack stack : stacks) {
            if(stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh

            ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
            TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
            if(tar != null) {
                //We take a leap of faith and assume the required matches the found itemstack in terms of crafting matching
                //It should match since we literally check in the same tick as we finish the recipe if it's valid...
                ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
                if(required.getFluidTypeAndAmount() != null) {
                    if (!found.isEmpty()) {
                        FluidActionResult fas = ItemUtils.drainFluidFromItem(found, required.getFluidTypeAndAmount(), true);
                        if (fas.isSuccess()) {
                            tar.getInventoryHandler().setStackInSlot(0, fas.getResult());
                            tar.markForUpdate();
                        }
                    }
                } else if(!ForgeHooks.getContainerItem(found).isEmpty()) {
                    tar.getInventoryHandler().setStackInSlot(0, ForgeHooks.getContainerItem(found));
                    tar.markForUpdate();
                } else {
                    ItemUtils.decrStackInInventory(tar.getInventoryHandler(), 0);
                    tar.markForUpdate();
                }
            }
        }
    }

    @Nonnull
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.RADIANCE;
    }

    @Override
    public void onCraftServerFinish(TileGodAltar altar, Random rand) {
        super.onCraftServerFinish(altar, rand);
        clearOuterConstellationCrystals(altar);
        chaliceSearchCaches.remove(getAltarCacheKey(altar));
    }

    @Override
    public void onCraftServerAbort(TileGodAltar altar, Random rand) {
        super.onCraftServerAbort(altar, rand);
        clearOuterConstellationCrystals(altar);
        chaliceSearchCaches.remove(getAltarCacheKey(altar));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileGodAltar altar, AddedActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);
        Vector3 thisAltar = new Vector3(altar).add(0.5, 0.5, 0.5);

        AddedActiveCraftingTask act = altar.getAddedActiveCraftingTask();
        if(act != null) {
            List<CraftingFocusStack> stacks = collectCurrentStacks(act.getCraftingData());
            for (CraftingFocusStack stack : stacks) {
                if(stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh

                ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
                TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
                if(tar != null) { //If it's null then the server messed up or we're desynced..
                    ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
                    if(!found.isEmpty() && required.matchCrafting(found)) {
                        Color c = ItemColorizationHelper.getDominantColorFromItemStack(found);
                        if(c == null) {
                            c = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                        }
                        if(ClientScheduler.getClientTick() % 35 == 0) {
                            EffectLightbeam beam = EffectHandler.getInstance().lightbeam(
                                    new Vector3(tar).add(0.5, 0.1, 0.5),
                                    new Vector3(altar).add(0.5, 4.5, 0.5),
                                    0.8);
                            beam.setColorOverlay(c);
                        }
                        if(rand.nextBoolean()) {
                            Vector3 at = new Vector3(tar);
                            at.add(rand.nextFloat() * 0.8 + 0.1, 0, rand.nextFloat() * 0.8 + 0.1);
                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                            p.setAlphaMultiplier(0.7F);
                            p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                            p.gravity(0.01).scale(0.3F + rand.nextFloat() * 0.1F);
                            p.setColor(c);
                            if(rand.nextInt(3) == 0) {
                                p.gravity(0.004).scale(0.1F + rand.nextFloat() * 0.1F);
                                p.setColor(Color.WHITE);
                            }
                        }
                        if(rand.nextInt(5) == 0) {
                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                                    altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                                    altar.getPos().getY() + 0.02,
                                    altar.getPos().getZ() - 3 + rand.nextFloat() * 7
                            );
                            p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.15F);
                            p.setColor(c);
                        }
                    } else {
                        NonNullList<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                        if(stacksApplicable.size() > 0) {
                            int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                            ItemStack element = stacksApplicable.get(MathHelper.floor(
                                    MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                            Color c = ItemColorizationHelper.getDominantColorFromItemStack(element);
                            if(c == null) {
                                c = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                            }
                            if(ClientScheduler.getClientTick() % 35 == 0) {
                                EffectLightbeam beam = EffectHandler.getInstance().lightbeam(
                                        new Vector3(tar).add(0.5, 0.1, 0.5),
                                        new Vector3(altar).add(0.5, 4.5, 0.5),
                                        0.8);
                                beam.setColorOverlay(c);
                            }
                            if(rand.nextBoolean()) {
                                Vector3 at = new Vector3(tar);
                                at.add(rand.nextFloat() * 0.8 + 0.1, 0, rand.nextFloat() * 0.8 + 0.1);
                                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                                p.setAlphaMultiplier(0.7F);
                                p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                                p.gravity(0.01).scale(0.3F + rand.nextFloat() * 0.1F);
                                p.setColor(c);
                                if(rand.nextInt(3) == 0) {
                                    p.gravity(0.004).scale(0.1F + rand.nextFloat() * 0.1F);
                                    p.setColor(Color.WHITE);
                                }
                            }
                        }
                    }
                }
            }

            spawnOuterConstellationGuideBeams(altar, act.getTicksCrafting());
        }

        if(state == AddedActiveCraftingTask.CraftingState.ACTIVE) {
            EntityFXFacingParticle p;
            if(rand.nextInt(4) == 0) {
                p = EffectHelper.genericFlareParticle(
                        altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                        altar.getPos().getY() + 0.02,
                        altar.getPos().getZ() - 3 + rand.nextFloat() * 7
                );
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.15F);
                p.setColor(Color.WHITE);
            }

            for (int i = 0; i < 1; i++) {
                Vector3 r = Vector3.random().setY(0).normalize().multiply(1.3 + rand.nextFloat() * 0.5).add(thisAltar.clone().addY(2 +  + rand.nextFloat() * 0.4));
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(Color.WHITE);
            }
            for (int i = 0; i < 2; i++) {
                Vector3 r = Vector3.random().setY(0).normalize().multiply(2 + rand.nextFloat() * 0.5).add(thisAltar.clone().addY(1.1 + rand.nextFloat() * 0.4));
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(Color.WHITE);
            }

            if(rand.nextInt(20) == 0) {
                Vector3 from = new Vector3(
                        altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                        altar.getPos().getY() + 0.02,
                        altar.getPos().getZ() - 3 + rand.nextFloat() * 7);
                MiscUtils.applyRandomOffset(from, rand, 0.4F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
                lightbeam.setMaxAge(64);
                lightbeam.setColorOverlay(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnOuterConstellationGuideBeams(TileGodAltar altar, int craftTick) {
        if (requiredOuterConstellations.isEmpty()) {
            return;
        }
        if (craftTick < constellationBegin) {
            return;
        }
        if (ClientScheduler.getClientTick() % 12 != 0) {
            return;
        }

        Vector3 source = new Vector3(altar).add(0.5, 8.3, 0.5);
        for (BlockPos offset : outerCrystalOffsets) {
            TileCollectorCrystal crystal = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(offset), TileCollectorCrystal.class, true);
            if (crystal == null) {
                continue;
            }

            Vector3 target = new Vector3(crystal).add(0.5, 0.6, 0.5);
            EffectLightbeam beam = EffectHandler.getInstance().lightbeam(target, source, 0.50);
            beam.setColorOverlay(isCelestial(crystal) ? new Color(25,25,112) : new Color(135,206,235));
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderOuterLiquidStarlightGuideMultiblock(TileGodAltar altar, int craftTick, long renderTick, double renderX, double renderY, double renderZ, float partialTicks) {
        String key = altar.getWorld().provider.getDimension() + ":" + altar.getPos().toLong();
        OuterLiquidGuideRenderState state = outerLiquidGuideRenderStates.computeIfAbsent(key, k -> new OuterLiquidGuideRenderState());

        long nowTick = ClientScheduler.getClientTick();
        if (state.lastTick != nowTick) {
            int fluidNow = altar.getFluidAmount();
            boolean inLiquidWindow = craftTick >= liquidStarlightBegin && craftTick <= liquidStarlightEnd;
            boolean needsLiquid = requiredLiquidStarlight > 0 && fluidNow < requiredLiquidStarlight;
            boolean inputIncreased = state.lastFluidAmount >= 0 && fluidNow > state.lastFluidAmount;
            boolean inputStopped = inLiquidWindow && needsLiquid && !inputIncreased;

            state.prevAlpha = state.alpha;
            if (inputIncreased || !inLiquidWindow || !needsLiquid) {
                state.waitingForDelay = false;
                state.stopStartTick = -1L;
            } else if (inputStopped) {
                if (!state.waitingForDelay) {
                    state.waitingForDelay = true;
                    state.stopStartTick = nowTick;
                }
            }

            boolean readyToShow = state.waitingForDelay && state.stopStartTick >= 0L && (nowTick - state.stopStartTick) >= liquidGuideDelayTicks;
            if (readyToShow && !state.showing) {
                // Lock the render Y for this visible cycle to avoid pop-up while fading.
                state.showing = true;
                state.lockedLiftY = fluidNow > 0 ? 1.0D : 0.0D;
            }
             float targetAlpha = readyToShow ? liquidGuideMaxAlpha : 0F;
             float step = readyToShow ? liquidGuideFadeInStep : liquidGuideFadeOutStep;
             if (state.alpha < targetAlpha) {
                 state.alpha = Math.min(targetAlpha, state.alpha + step);
             } else if (state.alpha > targetAlpha) {
                 state.alpha = Math.max(targetAlpha, state.alpha - step);
             }
            if (!readyToShow && state.alpha <= 0.001F) {
                state.showing = false;
                state.lockedLiftY = 0.0D;
            }

             if (!inLiquidWindow && state.alpha <= 0.001F) {
                 outerLiquidGuideRenderStates.remove(key);
                 return;
             }

            state.lastFluidAmount = fluidNow;
            state.lastTick = nowTick;
        }

        if (renderTick >= 0 || state.alpha <= 0.001F) {
            return;
        }

        float alpha = MathHelper.clamp(state.prevAlpha + (state.alpha - state.prevAlpha) * partialTicks, 0F, liquidGuideMaxAlpha);
        if (alpha <= 0.001F) {
            return;
        }

        LiquidGuidePatternMetrics metrics = getLiquidGuidePatternMetrics();
        if (metrics.pattern.isEmpty()) {
            return;
        }

        double angle = ((ClientScheduler.getClientTick() + partialTicks) / 20.0F) * (180F / (float) Math.PI);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        int prevAO = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
        int prevShadeModel = GL11.glGetInteger(GL11.GL_SHADE_MODEL);

         GlStateManager.pushMatrix();
        GlStateManager.translate(renderX + 0.5D, renderY + 1.5D + state.lockedLiftY, renderZ + 0.5D);
         GlStateManager.rotate((float) angle, 0F, 1F, 0F);
         GlStateManager.scale(metrics.scale, metrics.scale, metrics.scale);
         GlStateManager.translate(-metrics.centerX, -metrics.centerY, -metrics.centerZ);
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.disableCull();

        GL14.glBlendColor(1F, 1F, 1F, alpha);
        GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        Minecraft.getMinecraft().gameSettings.ambientOcclusion = 0;
        GlStateManager.shadeModel(GL11.GL_FLAT);
        TextureHelper.setActiveTextureToAtlasSprite();

        for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : metrics.pattern.entrySet()) {
            BlockPos offset = entry.getKey();
            IBlockState blockState = entry.getValue().state;

            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            GlStateManager.pushMatrix();
            GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());
            RenderingUtils.renderBlockSafely(liquidGuideAirWorld, BlockPos.ORIGIN, blockState, vb);
            tes.draw();
            GlStateManager.popMatrix();
        }

        IBlockState chalice = BlocksAS.blockChalice.getDefaultState();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        GlStateManager.pushMatrix();
        RenderingUtils.renderBlockSafely(liquidGuideAirWorld, BlockPos.ORIGIN, chalice, vb);
        tes.draw();
        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GL14.glBlendColor(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        Minecraft.getMinecraft().gameSettings.ambientOcclusion = prevAO;
        GlStateManager.shadeModel(prevShadeModel);
        GlStateManager.popMatrix();
        TextureHelper.refreshTextureBindState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileGodAltar altar, double x, double y, double z, float partialTicks) {
        super.onCraftTESRRender(altar, x, y, z, partialTicks);
        AddedActiveCraftingTask act = altar.getAddedActiveCraftingTask();
        if(act != null) {
            List<CraftingFocusStack> stacks = collectCurrentStacks(act.getCraftingData());
            for (CraftingFocusStack stack : stacks) {
                if (stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh

                ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
                TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
                if(tar != null) {
                    ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
                    if(found.isEmpty() || !required.matchCrafting(found)) {
                        NonNullList<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                        int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                        ItemStack element = stacksApplicable.get(MathHelper.floor(
                                MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                        renderTranslucentItem(element, x + stack.offset.getX(), y + stack.offset.getY(), z + stack.offset.getZ(), partialTicks);
                    }
                } else {
                    NonNullList<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                    int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                    ItemStack element = stacksApplicable.get(MathHelper.floor(
                            MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                    renderTranslucentItem(element, x + stack.offset.getX(), y + stack.offset.getY(), z + stack.offset.getZ(), partialTicks);
                }
            }

            renderActiveOuterConstellations(altar, partialTicks);
            renderOuterLiquidStarlightGuideMultiblock(altar, act.getTicksCrafting(), -1L, x, y, z, partialTicks);
            renderRisingStarlightItemModel(x, y, z, partialTicks, act.getTicksCrafting(), act.getTotalCraftingTime(), altar);
        }
    }

    private boolean isCelestial(TileCollectorCrystal tileCollectorCrystal) {
        return tileCollectorCrystal.getType() == BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL;
    }

    @SideOnly(Side.CLIENT)
    private void renderActiveOuterConstellations(TileGodAltar altar, float partialTicks) {
        List<TileCollectorCrystal> crystals = findCrystals(altar);
        if (crystals.isEmpty() && outerConstellationRenderStates.isEmpty()) {
            return;
        }

        final double constellationScale = 0.08D;
        final double lineSizeScale = 0.3D;
        final double starSizeScale = 5D;
        final double depthSizeScale = 0.55D;

        float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(altar.getWorld()) * 0.8F;
        int max = 5000;
        int tick = (int) (ClientScheduler.getClientTick() % max);
        float halfAge = max / 2F;
        float tr = 1F - (Math.abs(halfAge - tick) / halfAge);
        tr *= 2;

        Set<String> touchedKeys = new HashSet<>();
        for (TileCollectorCrystal crystal : crystals) {
            IConstellation constellation = getCrystalConstellation(crystal);
            if (constellation == null) {
                continue;
            }

            String key = getOuterConstellationStateKey(crystal);
            touchedKeys.add(key);
            float animProgress = updateOuterConstellationAnimProgress(key, isCrystalActive(crystal));
            if (animProgress <= 0.001F) {
                continue;
            }

            double renderX = crystal.getPos().getX() + 0.5;
            double renderY = crystal.getPos().getY() + 0.5 + (1.05 - 0.5) * animProgress;
            double renderZ = crystal.getPos().getZ() + 0.5;

            float animatedSize = (1.35F + tr * 0.25F) * animProgress;
            double animatedConstellationScale = constellationScale * animProgress;
            double animatedLineSizeScale = lineSizeScale * animProgress;
            double animatedStarSizeScale = starSizeScale * animProgress;
            double animatedDepthSizeScale = depthSizeScale * animProgress;

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
            RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);
            AARenderConstellation.renderConstellationIntoWorld3D(
                    constellation,
                    constellation.getConstellationColor(),
                    new Vector3(renderX, renderY, renderZ),
                    animatedSize,
                    animatedConstellationScale,
                    animatedLineSizeScale,
                    animatedStarSizeScale,
                    animatedDepthSizeScale,
                    new RenderConstellation.BrightnessFunction() {
                        @Override
                        public float getBrightness() {
                            return 0.3F;
                        }
                    }
            );
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }

        cleanupOuterConstellationRenderStates(altar, touchedKeys);
    }

    @SideOnly(Side.CLIENT)
    private String getOuterConstellationStateKey(TileCollectorCrystal crystal) {
        return crystal.getWorld().provider.getDimension() + ":" + crystal.getPos().toLong();
    }

    @SideOnly(Side.CLIENT)
    private float updateOuterConstellationAnimProgress(String key, boolean active) {
        OuterConstellationRenderState state = outerConstellationRenderStates.computeIfAbsent(key, k -> new OuterConstellationRenderState());
        long nowTick = ClientScheduler.getClientTick();
        long deltaTick = state.lastTick < 0 ? 1 : Math.max(1L, nowTick - state.lastTick);
        state.lastTick = nowTick;

        float direction = active ? 1F : -1F;
        float speed = 0.125F;
        state.progress = MathHelper.clamp(state.progress + direction * speed * deltaTick, 0F, 1F);
        return state.progress;
    }

    @SideOnly(Side.CLIENT)
    private void cleanupOuterConstellationRenderStates(TileGodAltar altar, Set<String> touchedKeys) {
        if (outerConstellationRenderStates.isEmpty()) {
            return;
        }

        List<String> cleanupKeys = new ArrayList<>(outerCrystalOffsets.length);
        int dim = altar.getWorld().provider.getDimension();
        for (BlockPos offset : outerCrystalOffsets) {
            cleanupKeys.add(dim + ":" + altar.getPos().add(offset).toLong());
        }

        for (String key : cleanupKeys) {
            if (touchedKeys.contains(key)) {
                continue;
            }

            OuterConstellationRenderState state = outerConstellationRenderStates.get(key);
            if (state == null) {
                continue;
            }
            if (state.progress <= 0.001F) {
                outerConstellationRenderStates.remove(key);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderTranslucentItem(ItemStack stack, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();

        IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
        float sinBobY = MathHelper.sin((ClientScheduler.getClientTick() + partialTicks) / 10.0F) * 0.1F + 0.1F;
        GlStateManager.translate(x + 0.5, y + sinBobY + 0.25F, z + 0.5);
        float ageRotate = ((ClientScheduler.getClientTick() + partialTicks) / 20.0F) * (180F / (float)Math.PI);
        GlStateManager.rotate(ageRotate, 0.0F, 1.0F, 0.0F);
        bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GROUND, false);

        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        RenderingUtils.tryRenderItemWithColor(stack, bakedModel, Color.WHITE, 0.1F);

        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Blending.DEFAULT.applyStateManager();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    private void renderRisingStarlightItemModel(double x, double y, double z, float partialTicks, int progressTicks, int totalTicks, TileGodAltar altar) {
        if (totalTicks <= 0) {
            return;
        }

        float progress = (progressTicks + partialTicks) / (float) totalTicks;
        progress = MathHelper.clamp(progress, 0.0F, 1.0F);
        if (progress <= 0.0F) {
            return;
        }

        ItemStack stack = getOutputForRender();
        if (stack.isEmpty()) {
            return;
        }

        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(starlightLiquidStill.toString());
        if (sprite == null) {
            return;
        }
        int tintColor = getStarlightTintColor();
        IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
        if (bakedModel == null) {
            return;
        }
        boolean builtIn = bakedModel.isBuiltInRenderer();
        IBlockState modelState = null;
        boolean hasQuads = hasAnyQuads(bakedModel);
        if (stack.getItem() instanceof ItemBlock) {
            ItemBlock ib = (ItemBlock) stack.getItem();
            IBlockState state = ib.getBlock().getStateFromMeta(stack.getMetadata());
            IBakedModel blockModel = Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .getBlockModelShapes().getModelForState(state);
            if (blockModel != null && hasAnyQuads(blockModel, state)) {
                bakedModel = blockModel;
                builtIn = false;
                modelState = state;
                hasQuads = true;
            }
        }

        float targetClipHeight = this.requiredLiquidStarlight <= 0 ? 0F : (float) altar.getFluidAmount() / this.requiredLiquidStarlight;
        float clipHeight = getAnimatedRisingClipHeight(altar, targetClipHeight, partialTicks);

        if (clipHeight <= 0.0F) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.2, z + 0.5);

        float ageRotate = ((ClientScheduler.getClientTick() + partialTicks) / 20.0F) * (180F / (float)Math.PI);
        GlStateManager.rotate(ageRotate, 0.0F, 1.0F, 0.0F);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
        Blending.CONSTANT_ALPHA.applyStateManager();

        float scale = 0.7F;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-0.5F, 0.0F, -0.5F);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);


        if (clipHeight > 0) {

            enableClipPlane(clipHeight);
            boolean wantStencil = true;
            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                wantStencil = block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
            }
            boolean useStencil = wantStencil && setupStencilMask(bakedModel, stack, builtIn, modelState);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//            GlStateManager.depthMask(false);
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-1.0F, -1.0F);
            if (builtIn && !hasQuads) {
                renderStarlightCube(sprite, tintColor);
            } else {
                renderRetexturedModel(bakedModel, sprite, tintColor, modelState);
            }
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
//            GlStateManager.depthMask(true);
            if (useStencil) {
                GL11.glDisable(GL11.GL_STENCIL_TEST);
                GL11.glStencilMask(0xFF);
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
            }
        }

        GL11.glDisable(GL11.GL_CLIP_PLANE0);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        Blending.DEFAULT.applyStateManager();
        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    private void renderRetexturedModel(IBakedModel model, TextureAtlasSprite sprite, int color, @Nullable IBlockState state) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, getModelVertexFormat(model, state));

        long rand = 0L;
        boolean preferFaces = state != null;
        if (preferFaces) {
            boolean rendered = false;
            for (EnumFacing face : EnumFacing.values()) {
                List<BakedQuad> faceQuads = model.getQuads(state, face, rand);
                if (!faceQuads.isEmpty()) {
                    renderQuadList(buffer, faceQuads, sprite, color);
                    rendered = true;
                }
            }
            if (!rendered) {
                List<BakedQuad> general = model.getQuads(state, null, rand);
                if (!general.isEmpty()) {
                    renderQuadList(buffer, general, sprite, color);
                }
            }
        } else {
            List<BakedQuad> general = model.getQuads(state, null, rand);
            if (!general.isEmpty()) {
                renderQuadList(buffer, general, sprite, color);
            } else {
                for (EnumFacing face : EnumFacing.values()) {
                    renderQuadList(buffer, model.getQuads(state, face, rand), sprite, color);
                }
            }
        }

        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    private boolean hasAnyQuads(IBakedModel model) {
        return hasAnyQuads(model, null);
    }

    @SideOnly(Side.CLIENT)
    private boolean hasAnyQuads(IBakedModel model, @Nullable IBlockState state) {
        long rand = 0L;
        if (!model.getQuads(state, null, rand).isEmpty()) {
            return true;
        }
        for (EnumFacing face : EnumFacing.values()) {
            if (!model.getQuads(state, face, rand).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private VertexFormat getModelVertexFormat(IBakedModel model, @Nullable IBlockState state) {
        long rand = 0L;
        List<BakedQuad> general = model.getQuads(state, null, rand);
        if (!general.isEmpty()) {
            return general.get(0).getFormat();
        }
        for (EnumFacing face : EnumFacing.values()) {
            List<BakedQuad> quads = model.getQuads(state, face, rand);
            if (!quads.isEmpty()) {
                return quads.get(0).getFormat();
            }
        }
        return DefaultVertexFormats.ITEM;
    }

    @SideOnly(Side.CLIENT)
    private void renderStarlightCube(TextureAtlasSprite sprite, int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        float uMin = sprite.getMinU();
        float uMax = sprite.getMaxU();
        float vMin = sprite.getMinV();
        float vMax = sprite.getMaxV();

        float min = 0.0F;
        float max = 1.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        // Front (Z+)
        putVertex(buffer, min, min, max, uMin, vMin, r, g, b, a, 0.0F, 0.0F, 1.0F);
        putVertex(buffer, max, min, max, uMax, vMin, r, g, b, a, 0.0F, 0.0F, 1.0F);
        putVertex(buffer, max, max, max, uMax, vMax, r, g, b, a, 0.0F, 0.0F, 1.0F);
        putVertex(buffer, min, max, max, uMin, vMax, r, g, b, a, 0.0F, 0.0F, 1.0F);

        putVertex(buffer, max, min, min, uMin, vMin, r, g, b, a, 0.0F, 0.0F, -1.0F);
        putVertex(buffer, min, min, min, uMax, vMin, r, g, b, a, 0.0F, 0.0F, -1.0F);
        putVertex(buffer, min, max, min, uMax, vMax, r, g, b, a, 0.0F, 0.0F, -1.0F);
        putVertex(buffer, max, max, min, uMin, vMax, r, g, b, a, 0.0F, 0.0F, -1.0F);

        putVertex(buffer, min, min, min, uMin, vMin, r, g, b, a, -1.0F, 0.0F, 0.0F);
        putVertex(buffer, min, min, max, uMax, vMin, r, g, b, a, -1.0F, 0.0F, 0.0F);
        putVertex(buffer, min, max, max, uMax, vMax, r, g, b, a, -1.0F, 0.0F, 0.0F);
        putVertex(buffer, min, max, min, uMin, vMax, r, g, b, a, -1.0F, 0.0F, 0.0F);

        putVertex(buffer, max, min, max, uMin, vMin, r, g, b, a, 1.0F, 0.0F, 0.0F);
        putVertex(buffer, max, min, min, uMax, vMin, r, g, b, a, 1.0F, 0.0F, 0.0F);
        putVertex(buffer, max, max, min, uMax, vMax, r, g, b, a, 1.0F, 0.0F, 0.0F);
        putVertex(buffer, max, max, max, uMin, vMax, r, g, b, a, 1.0F, 0.0F, 0.0F);

        putVertex(buffer, min, max, min, uMin, vMin, r, g, b, a, 0.0F, 1.0F, 0.0F);
        putVertex(buffer, min, max, max, uMin, vMax, r, g, b, a, 0.0F, 1.0F, 0.0F);
        putVertex(buffer, max, max, max, uMax, vMax, r, g, b, a, 0.0F, 1.0F, 0.0F);
        putVertex(buffer, max, max, min, uMax, vMin, r, g, b, a, 0.0F, 1.0F, 0.0F);

        putVertex(buffer, min, min, max, uMin, vMax, r, g, b, a, 0.0F, -1.0F, 0.0F);
        putVertex(buffer, min, min, min, uMin, vMin, r, g, b, a, 0.0F, -1.0F, 0.0F);
        putVertex(buffer, max, min, min, uMax, vMin, r, g, b, a, 0.0F, -1.0F, 0.0F);
        putVertex(buffer, max, min, max, uMax, vMax, r, g, b, a, 0.0F, -1.0F, 0.0F);

        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    private void putVertex(BufferBuilder buffer, float x, float y, float z, float u, float v,
                           int r, int g, int b, int a, float nx, float ny, float nz) {
        buffer.pos(x, y, z).tex(u, v).color(r, g, b, a).normal(nx, ny, nz).endVertex();
    }

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite getStarlightSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(starlightLiquidStill.toString());
    }

    @SideOnly(Side.CLIENT)
    private int getStarlightTintColor() {
        Fluid fluid = FluidRegistry.getFluid("liquid_starlight");
        if (fluid == null) {
            fluid = FluidRegistry.getFluid("starlight");
        }
        int color = fluid != null ? fluid.getColor() : 0xFFFFFFFF;
        return (color & 0x00FFFFFF) | 0xB0000000;
    }

    @SideOnly(Side.CLIENT)
    private boolean setupStencilMask(IBakedModel model, ItemStack stack, boolean builtIn, @Nullable IBlockState state) {
        Framebuffer fb = Minecraft.getMinecraft().getFramebuffer();
        if (fb == null) {
            return false;
        }
        if (!fb.isStencilEnabled() && !fb.enableStencil()) {
            return false;
        }

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilMask(0xFF);

        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.depthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        if (builtIn) {
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        } else {
            renderOriginalModel(model, 0xFFFFFFFF, state);
        }

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilMask(0x00);
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void cleanupStencilMask() {
    }

    @SideOnly(Side.CLIENT)
    private void renderOriginalModel(IBakedModel model, int color, @Nullable IBlockState state) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, getModelVertexFormat(model, state));

        long rand = 0L;
        boolean preferFaces = state != null;
        if (preferFaces) {
            boolean rendered = false;
            for (EnumFacing face : EnumFacing.values()) {
                List<BakedQuad> faceQuads = model.getQuads(state, face, rand);
                if (!faceQuads.isEmpty()) {
                    renderQuadListOriginal(buffer, faceQuads, color);
                    rendered = true;
                }
            }
            if (!rendered) {
                List<BakedQuad> general = model.getQuads(state, null, rand);
                if (!general.isEmpty()) {
                    renderQuadListOriginal(buffer, general, color);
                }
            }
        } else {
            List<BakedQuad> general = model.getQuads(state, null, rand);
            if (!general.isEmpty()) {
                renderQuadListOriginal(buffer, general, color);
            } else {
                for (EnumFacing face : EnumFacing.values()) {
                    renderQuadListOriginal(buffer, model.getQuads(state, face, rand), color);
                }
            }
        }

        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    private void renderQuadList(BufferBuilder buffer, List<BakedQuad> quads, TextureAtlasSprite sprite, int color) {
        for (BakedQuad quad : quads) {
            BakedQuad retextured = new BakedQuadRetextured(quad, sprite);
            LightUtil.renderQuadColor(buffer, retextured, color);
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderQuadListOriginal(BufferBuilder buffer, List<BakedQuad> quads, int color) {
        for (BakedQuad quad : quads) {
            LightUtil.renderQuadColor(buffer, quad, color);
        }
    }

    @SideOnly(Side.CLIENT)
    private void enableClipPlane(float height) {
        DoubleBuffer clipPlane = BufferUtils.createDoubleBuffer(4);
        float h = height + 0.001F; // Avoid coplanar clipping flicker on top face
        clipPlane.put(0, 0.0);
        clipPlane.put(1, -1.0);
        clipPlane.put(2, 0.0);
        clipPlane.put(3, h);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, clipPlane);
        GL11.glEnable(GL11.GL_CLIP_PLANE0);
    }

    @SideOnly(Side.CLIENT)
    private float getAnimatedRisingClipHeight(TileGodAltar altar, float targetClipHeight, float partialTicks) {
        String key = getAltarCacheKey(altar);
        RisingClipRenderState state = risingClipRenderStates.computeIfAbsent(key, k -> new RisingClipRenderState());
        long nowTick = ClientScheduler.getClientTick();
        long deltaTick = state.lastTick < 0 ? 1L : Math.max(1L, nowTick - state.lastTick);
        state.lastTick = nowTick;

        targetClipHeight = MathHelper.clamp(targetClipHeight, 0F, 1F);
        state.prevClipHeight = state.clipHeight;
        if (targetClipHeight > state.clipHeight) {
            state.clipHeight = Math.min(targetClipHeight, state.clipHeight + risingClipIncreasePerTick * deltaTick);
        } else {
            // Keep falling behavior responsive; only smooth the rising edge.
            state.clipHeight = targetClipHeight;
        }

        float interpolated = state.prevClipHeight + (state.clipHeight - state.prevClipHeight) * MathHelper.clamp(partialTicks, 0F, 1F);
        if (targetClipHeight <= 0.001F && interpolated <= 0.001F) {
            risingClipRenderStates.remove(key);
        }
        return MathHelper.clamp(interpolated, 0F, 1F);
    }

    @Nullable
    protected BlockPos findUnusedRelay(TileGodAltar center, List<CraftingFocusStack> found) {
        List<BlockPos> eligableRelayOffsets = Lists.newLinkedList();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                if(xx == 0 && zz == 0) continue; //Not that it matters tho

                BlockPos offset = new BlockPos(xx, 0, zz);
                TileAttunementRelay tar = MiscUtils.getTileAt(center.getWorld(), center.getPos().add(offset), TileAttunementRelay.class, true);
                if(tar != null) {
                    eligableRelayOffsets.add(offset);
                }
            }
        }
        for (CraftingFocusStack stack : found) {
            eligableRelayOffsets.remove(stack.offset);
        }
        if(eligableRelayOffsets.size() <= 0) {
            return null;
        }
        return eligableRelayOffsets.get(center.getWorld().rand.nextInt(eligableRelayOffsets.size()));
    }

    @Nullable
    protected TileChalice findChalice(TileGodAltar center, int liquidStarlightRequired) {
        String key = getAltarCacheKey(center);
        ChaliceSearchCache cache = chaliceSearchCaches.computeIfAbsent(key, k -> new ChaliceSearchCache());
        long nowTick = center.getWorld().getTotalWorldTime();

        if (cache.shouldStartScan(nowTick)) {
            cache.beginScan(center.isPatternAltarGod2Active() ? 8 : 6);
        }

        if (cache.scanning) {
            scanChalicesStep(center, cache, chaliceScanBudgetPerTick);
        }

        List<TileChalice> valid = getValidChalicesFromCache(center, cache, liquidStarlightRequired);
        if(valid.isEmpty()) {
            return null;
        }
        return valid.get(center.getWorld().rand.nextInt(valid.size()));
    }

    private void scanChalicesStep(TileGodAltar center, ChaliceSearchCache cache, int budget) {
        if (!cache.scanning || budget <= 0) {
            return;
        }

        BlockPos base = center.getPos();
        MultiblockContainmentChalice multiblock = new MultiblockContainmentChalice();
        while (budget-- > 0 && cache.scanning) {
            int xx = cache.scanX;
            int yy = cache.scanY;
            int zz = cache.scanZ;

            cache.advanceCursor();

            if (xx <= cache.closeRange && zz <= cache.closeRange) {
                continue; // 太近了
            }

            BlockPos pos = base.add(xx, yy, zz);
            TileChalice chalice = MiscUtils.getTileAt(center.getWorld(), pos, TileChalice.class, true);
            if (chalice == null || chalice.getHeldFluid() != BlocksAS.fluidLiquidStarlight) {
                continue;
            }
            if (multiblock.matches(center.getWorld(), pos)) {
                cache.candidates.add(pos);
            }
        }

        if (!cache.scanning) {
            cache.lastScanTick = center.getWorld().getTotalWorldTime();
        }
    }

    private List<TileChalice> getValidChalicesFromCache(TileGodAltar center, ChaliceSearchCache cache, int needed) {
        List<TileChalice> valid = new ArrayList<>();
        Iterator<BlockPos> iterator = cache.candidates.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            TileChalice chalice = MiscUtils.getTileAt(center.getWorld(), pos, TileChalice.class, true);
            if (chalice == null || chalice.getHeldFluid() != BlocksAS.fluidLiquidStarlight) {
                iterator.remove();
                continue;
            }
            if (chalice.getFluidAmount() >= needed) {
                valid.add(chalice);
            }
        }
        return valid;
    }

    private String getAltarCacheKey(TileGodAltar altar) {
        return altar.getWorld().provider.getDimension() + ":" + altar.getPos().toLong();
    }

    @SideOnly(Side.CLIENT)
    private static LiquidGuidePatternMetrics getLiquidGuidePatternMetrics() {
        if (liquidGuidePatternMetrics != null) {
            return liquidGuidePatternMetrics;
        }

        Map<BlockPos, BlockArray.BlockInformation> pattern = liquidGuidePattern.getPattern();
        int minX = 0, minY = 0, minZ = 0;
        int maxX = 0, maxY = 0, maxZ = 0;
        for (BlockPos p : pattern.keySet()) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;
        float sizeMax = Math.max(1F, Math.max(sizeX, Math.max(sizeY, sizeZ)));
        float scale = 0.92F / sizeMax;

        liquidGuidePatternMetrics = new LiquidGuidePatternMetrics(
                pattern,
                (minX + maxX + 1) / 2D,
                (minY + maxY + 1) / 2D,
                (minZ + maxZ + 1) / 2D,
                scale
        );
        return liquidGuidePatternMetrics;
    }

    protected boolean matchFocusStacks(TileGodAltar altar, List<CraftingFocusStack> stacks) {
        for (CraftingFocusStack stack : stacks) {
            int index = stack.stackIndex;
            if(index < 0 || index >= additionallyRequiredStacks.size()) continue;
            ItemHandle required = additionallyRequiredStacks.get(index);
            TileAttunementRelay relay = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
            if(relay == null) {
                return false;
            }
            ItemStack in = relay.getInventoryHandler().getStackInSlot(0);
            if(in.isEmpty() || !required.matchCrafting(in)) {
                return false;
            }
        }
        return true;
    }

    protected void storeCurrentStacks(NBTTagCompound craftingStorage, List<CraftingFocusStack> stacks) {
        NBTTagList list = new NBTTagList();
        for (CraftingFocusStack stack : stacks) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("focusIndex", stack.stackIndex);
            NBTHelper.writeBlockPosToNBT(stack.offset, tag);
            list.appendTag(tag);
        }
        craftingStorage.setTag("offsetFocusList", list);
    }

    public static List<CraftingFocusStack> collectCurrentStacks(NBTTagCompound craftingStorage) {
        List<CraftingFocusStack> stacks = Lists.newLinkedList();
        NBTTagList list = craftingStorage.getTagList("offsetFocusList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound cmp = list.getCompoundTagAt(i);
            int index = cmp.getInteger("focusIndex");
            BlockPos pos = NBTHelper.readBlockPosFromNBT(cmp);
            stacks.add(new CraftingFocusStack(index, pos));
        }
        return stacks;
    }



    public static class CraftingFocusStack {

        public final Integer stackIndex; //Index of required stack
        public final BlockPos offset;

        protected CraftingFocusStack(Integer stackIndex, BlockPos offset) {
            this.stackIndex = stackIndex;
            this.offset = offset;
        }

    }

    public static enum GodRecipeSlot {
        UPPER_LEFT(26),
        UPPER_CENTER(27),
        UPPER_RIGHT(28),
        LOWER_LEFT(29),
        LOWER_CENTER(30),
        LOWER_RIGHT(31),

        LEFT_UPPER(32),
        LEFT_CENTER(33),
        LEFT_LOWER(34),
        RIGHT_UPPER(35),
        RIGHT_CENTER(36),
        RIGHT_LOWER(37),

        UPPER_LEFT_CORNER(38),
        UPPER_RIGHT_CORNER(39),
        LOWER_LEFT_CORNER(40),
        LOWER_RIGHT_CORNER(41);

        private final int slotId;

        GodRecipeSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

    @SideOnly(Side.CLIENT)
    private static class OuterConstellationRenderState {
        private float progress = 0F;
        private long lastTick = -1L;
    }

    @SideOnly(Side.CLIENT)
    private static class OuterLiquidGuideRenderState {
        private int lastFluidAmount = -1;
        private long stopStartTick = -1L;
        private boolean waitingForDelay = false;
        private boolean showing = false;
        private double lockedLiftY = 0.0D;
        private float alpha = 0F;
        private float prevAlpha = 0F;
        private long lastTick = Long.MIN_VALUE;
    }

    @SideOnly(Side.CLIENT)
    private static class RisingClipRenderState {
        private float clipHeight = 0F;
        private float prevClipHeight = 0F;
        private long lastTick = -1L;
    }

    private static class ChaliceSearchCache {
        private long lastScanTick = Long.MIN_VALUE;
        private final List<BlockPos> candidates = new ArrayList<>();
        private boolean scanning = false;
        private int scanX = -chaliceScanRangeXZ;
        private int scanY = -chaliceScanRangeY;
        private int scanZ = -chaliceScanRangeXZ;
        private int closeRange = 6;

        private boolean shouldStartScan(long nowTick) {
            return !scanning && (nowTick - lastScanTick >= chaliceRescanIntervalTicks || candidates.isEmpty());
        }

        private void beginScan(int closeRange) {
            this.candidates.clear();
            this.closeRange = closeRange;
            this.scanX = -chaliceScanRangeXZ;
            this.scanY = -chaliceScanRangeY;
            this.scanZ = -chaliceScanRangeXZ;
            this.scanning = true;
        }

        private void advanceCursor() {
            scanY++;
            if (scanY > chaliceScanRangeY) {
                scanY = -chaliceScanRangeY;
                scanZ++;
                if (scanZ > chaliceScanRangeXZ) {
                    scanZ = -chaliceScanRangeXZ;
                    scanX++;
                    if (scanX > chaliceScanRangeXZ) {
                        scanning = false;
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static class LiquidGuidePatternMetrics {
        private final Map<BlockPos, BlockArray.BlockInformation> pattern;
        private final double centerX;
        private final double centerY;
        private final double centerZ;
        private final float scale;

        private LiquidGuidePatternMetrics(Map<BlockPos, BlockArray.BlockInformation> pattern, double centerX, double centerY, double centerZ, float scale) {
            this.pattern = pattern;
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.scale = scale;
        }
    }

    public static class WorldBlockArrayRenderAccess implements IBlockAccess {

        private WorldBlockArrayRenderAccess() {
        }


        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 0;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return true;
        }

        @Nonnull
        @Override
        @SideOnly(Side.CLIENT)
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }

        @Nonnull
        @Override
        @SideOnly(Side.CLIENT)
        public WorldType getWorldType() {
            return Minecraft.getMinecraft().world.getWorldType();
        }

        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
            return _default;
        }
    }

}
