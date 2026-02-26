package com.wdcftgg.astralaltar.crafting.recipe;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
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
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
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
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import com.wdcftgg.astralaltar.cilent.render.BakedQuadRetextured;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
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
import org.lwjgl.BufferUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GodRecipe extends AddedAbstractAltarRecipe implements IAddedCraftingProgress {

    private static final ResourceLocation starlightLiquidStill = new ResourceLocation("astralsorcery:blocks/fluid/starlight_still");



    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();
    private Map<GodRecipeSlot, ItemHandle> matchGodStacks = new HashMap<>();
    private IConstellation requiredConstellation = null;
    private Map<TraitRecipeSlot, ItemHandle> matchTraitStacks = new HashMap<>();
    private int liquidStarlightRequired = 0;

    private static Vector3[] offsetPillars = new Vector3[] {
            new Vector3( 4, 3,  4),
            new Vector3(-4, 3,  4),
            new Vector3( 4, 3, -4),
            new Vector3(-4, 3, -4)
    };

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
        this.liquidStarlightRequired = liquidStarlight;
        return this;
    }

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
        return 1000;
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
        int liquidStarlightRequired = this.liquidStarlightRequired;
        boolean noAdditionalItems = required <= 0; //No additional items, huh.
        boolean noLiquidStarlightNeeded = liquidStarlightRequired <= 0; //No additional items, huh.

        boolean finishOuterItem = true;

        if (!noAdditionalItems) {
            int part = 350;
            int offset = 70;
            int cttPart = part / required;
            for (int i = 0; i < required; i++) {
                int timing = (i * cttPart) + offset;
                if (activeCraftingTick >= timing) {
                    CraftingFocusStack found = null;
                    for (CraftingFocusStack stack : stacks) {
                        if (stack.stackIndex == i) {
                            found = stack;
                            break;
                        }
                    }
                    if (found == null) {
                        BlockPos next = findUnusedRelay(altar, stacks);
                        if (next != null) {
                            CraftingFocusStack stack = new CraftingFocusStack(i, next);
                            stacks.add(stack);
                            storeCurrentStacks(craftingData, stacks);
                        }
                        finishOuterItem = false;
                        return false;
                    }
                }
            }
        }
        
        if (!noLiquidStarlightNeeded) {
            int allTime = 300;
            int totalSteps = 30;
            int begin = (required * 350 / required) + 170;
            int cttPart = allTime / totalSteps;
//            int totalTime = ((liquidStarlightRequired - altar.getFluidAmount()) / mbNeeded);
//            for (int i = 30 - totalTime ; i < totalTime; i++) {
//                int timing = (i * cttPart) + begin;
////                if (activeCraftingTick >= timing) {
////                    System.out.println("activeCraftingTick--" + activeCraftingTick);
////                    System.out.println("timing--" + timing);
////                    System.out.println("i--" + i);
////                }
//                if (activeCraftingTick == timing) {
//                    if (!findChaliceAndFluidTransfer(altar, mbNeeded)) {
//                        return false;
//                    }
//                }
//            }
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

            if (altar.getFluidAmount() < this.liquidStarlightRequired) {
                int timing = (30 * cttPart) + begin;
                if (activeCraftingTick == timing) {
                    int lastNeeded = this.liquidStarlightRequired - altar.getFluidAmount();
                    return findChaliceAndFluidTransfer(altar, lastNeeded);
                }
            }
        }

        return true;
    }

    private boolean findChaliceAndFluidTransfer(TileGodAltar altar, int needed) {
        TileChalice chalice = findChalice(altar, needed);

        if (chalice == null) return false;

        FluidStack fluidStack = new FluidStack(BlocksAS.fluidLiquidStarlight, needed);
        LiquidStarlightChaliceHandler.doFluidTransfer(chalice, altar, fluidStack.copy());
        chalice.getTank().drain(needed, true);
        System.out.println("needed--" + needed);
        return true;
    }

    @Override
    public boolean tryProcess(TileGodAltar altar, NBTTagCompound craftingData, int activeCraftingTick, int totalCraftingTime) {
        return tryProcess(altar, null, craftingData, activeCraftingTick, totalCraftingTime);
//        return tryProcess(altar, new ActiveCraftingTask(abstractAltarRecipe, abstractAltarRecipe.craftingTickTime(), craftingTask.getPlayerCraftingUUID()), craftingData, activeCraftingTick, totalCraftingTime);
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

            renderRisingStarlightItemModel(x, y, z, partialTicks, act.getTicksCrafting(), act.getTotalCraftingTime(), altar);
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

        float clipHeight = (float) altar.getFluidAmount() / this.liquidStarlightRequired;

        if (clipHeight <= 0.0F) {
            GL11.glDisable(GL11.GL_CLIP_PLANE0);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            Blending.DEFAULT.applyStateManager();
            GlStateManager.popMatrix();
            return;
        }

        if (clipHeight > 0) {

            enableClipPlane(clipHeight);
            boolean wantStencil = true;
            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                wantStencil = block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
            }
            boolean useStencil = wantStencil && setupStencilMask(bakedModel, stack, builtIn, modelState);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.depthMask(false);
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-1.0F, -1.0F);
            if (builtIn && !hasQuads) {
                renderStarlightCube(sprite, tintColor);
            } else {
                renderRetexturedModel(bakedModel, sprite, tintColor, modelState);
            }
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.depthMask(true);
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
        List<TileChalice> chaliceOffsets = Lists.newLinkedList();
        for (int xx = -15; xx <= 15; xx++) {
            for (int zz = -15; zz <= 15; zz++) {
                for (int yy = -5; yy <= 5; yy++) {
                    if (xx <= 6 && zz <= 6) continue; // 太近了

                    BlockPos offset = new BlockPos(xx, yy, zz);
                    TileChalice tar = MiscUtils.getTileAt(center.getWorld(), center.getPos().add(offset), TileChalice.class, true);
                    if (tar != null) {
                        // 是星能液且数量够一次的抽取量
                        if (tar.getFluidAmount() >= liquidStarlightRequired &&
                                tar.getHeldFluid() == BlocksAS.fluidLiquidStarlight) {
                            MultiblockContainmentChalice m = new MultiblockContainmentChalice();
                            if (m.matches(center.getWorld(), center.getPos().add(offset)))
                                chaliceOffsets.add(tar);
                        }
                    }
                }
            }
        }
        if(chaliceOffsets.size() <= 0) {
            return null;
        }
        return chaliceOffsets.get(center.getWorld().rand.nextInt(chaliceOffsets.size()));
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

}
