package com.wdcftgg.astralaltar.crafting.recipe;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.AddedAbstractAltarRecipe;
import com.wdcftgg.astralaltar.crafting.AddedActiveCraftingTask;
import com.wdcftgg.astralaltar.crafting.IAddedCraftingProgress;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GodRecipe extends AddedAbstractAltarRecipe implements IAddedCraftingProgress {

    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();
    private Map<GodRecipeSlot, ItemHandle> matchGodStacks = new HashMap<>();
    private IConstellation requiredConstellation = null;
    private Map<TraitRecipeSlot, ItemHandle> matchTraitStacks = new HashMap<>();

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

    public GodRecipe setInnerGodItem(Item i, GodRecipeSlot... slots) {
        return setInnerGodItem(new ItemStack(i), slots);
    }

    public GodRecipe setInnerGodItem(Block b, GodRecipeSlot... slots) {
        return setInnerGodItem(new ItemStack(b), slots);
    }

    public GodRecipe setInnerGodItem(ItemStack stack, GodRecipeSlot... slots) {
        return setInnerGodItem(new ItemHandle(stack), slots);
    }

    public GodRecipe setInnerGodItem(String oreDict, GodRecipeSlot... slots) {
        return setInnerGodItem(new ItemHandle(oreDict), slots);
    }

    public GodRecipe setInnerGodItem(FluidStack fluid, GodRecipeSlot... slots) {
        return setInnerGodItem(new ItemHandle(fluid), slots);
    }

    public GodRecipe setInnerGodItem(Fluid fluid, int mbAmount, GodRecipeSlot... slots) {
        return setInnerGodItem(new FluidStack(fluid, mbAmount), slots);
    }

    public GodRecipe setInnerGodItem(Fluid fluid, GodRecipeSlot... slots) {
        return setInnerGodItem(fluid, 1000, slots);
    }

    public GodRecipe setInnerGodItem(ItemHandle handle, GodRecipeSlot... slots) {
        for (GodRecipeSlot slot : slots) {
            matchGodStacks.put(slot, handle);
        }
        return this;
    }

//    public GodRecipe addOuterGodItem(Item i) {
//        return addOuterGodItem(new ItemStack(i));
//    }
//
//    public GodRecipe addOuterGodItem(Block b) {
//        return addOuterGodItem(new ItemStack(b));
//    }

//    public GodRecipe addOuterGodItem(ItemStack stack) {
//        return addOuterGodItem(new ItemHandle(stack));
//    }
//
//    public GodRecipe addOuterGodItem(String oreDict) {
//        return addOuterGodItem(new ItemHandle(oreDict));
//    }
//
//    public GodRecipe addOuterGodItem(FluidStack fluid) {
//        return addOuterGodItem(new ItemHandle(fluid));
//    }

//    public GodRecipe addOuterGodItem(Fluid fluid, int mbAmount) {
//        return addOuterGodItem(new FluidStack(fluid, mbAmount));
//    }

//    public GodRecipe addOuterGodItem(Fluid fluid) {
//        return addOuterGodItem(fluid, 1000);
//    }

//    public GodRecipe addOuterGodItem(ItemHandle handle) {
//        additionallyRequiredStacks.add(handle);
//        return this;
//    }

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
    public List<ItemStack> getInnerGodItems(GodRecipeSlot slot) {
        ItemHandle handle = matchGodStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getInnerGodItemHandle(GodRecipeSlot slot) {
        return matchGodStacks.get(slot);
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
        return 700;
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

        for (TraitRecipe.TraitRecipeSlot slot : TraitRecipe.TraitRecipeSlot.values()) {
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

        this.consumeOuterInputs(ta, craftingTask);
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
        if (required <= 0) {
            return true; //No additional items, huh.
        }

        int part = totalCraftingTime / 2;
        int offset = totalCraftingTime / 10;
        int cttPart = part / required;
        for (int i = 0; i < required; i++) {
            int timing = (i * cttPart) + offset;
            if(activeCraftingTick >= timing) {
                CraftingFocusStack found = null;
                for (CraftingFocusStack stack : stacks) {
                    if(stack.stackIndex == i) {
                        found = stack;
                        break;
                    }
                }
                if(found == null) {
                    BlockPos next = findUnusedRelay(altar, stacks);
                    if(next != null) {
                        CraftingFocusStack stack = new CraftingFocusStack(i, next);
                        stacks.add(stack);
                        storeCurrentStacks(craftingData, stacks);
                    }
                    return false;
                }
            }
        }
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
                ItemStack altarItem = invHandler.getStackInSlot(slot.slotId);
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.slotId).isEmpty()) return false;
            }
        }

        for (ConstellationAtlarSlot slot : ConstellationAtlarSlot.values()) {
            ItemHandle expected = matchStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.slotId);
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.slotId).isEmpty()) return false;
            }
        }

//        IConstellation req = getRequiredConstellation();
//        if(req != null) {
//            IConstellation focus = altar.getFocusedConstellation();
//            if(focus == null) return false;
//            if(!req.equals(focus)) return false;
//        }
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
        System.out.println("additionallyRequiredStacks.size()--" + additionallyRequiredStacks.size());
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

    public static enum AttunementAltarSlot {

        UPPER_LEFT(9),
        UPPER_RIGHT(10),
        LOWER_LEFT(11),
        LOWER_RIGHT(12);

        private final int slotId;

        private AttunementAltarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

    public static enum ConstellationAtlarSlot {

        UP_UP_LEFT(13),
        UP_UP_RIGHT(14),
        UP_LEFT_LEFT(15),
        UP_RIGHT_RIGHT(16),

        DOWN_LEFT_LEFT(17),
        DOWN_RIGHT_RIGHT(18),
        DOWN_DOWN_LEFT(19),
        DOWN_DOWN_RIGHT(20);

        private final int slotId;

        ConstellationAtlarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }
    }

    public static enum TraitRecipeSlot {

        UPPER_CENTER(21),
        LEFT_CENTER(22),
        RIGHT_CENTER(23),
        LOWER_CENTER(24);

        private final int slotId;

        TraitRecipeSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

    public static enum GodRecipeSlot {

        SLOT1(26),
        SLOT2(27),
        SLOT3(28),
        SLOT4(29),
        SLOT5(30),
        SLOT6(31),
        SLOT7(32),
        SLOT8(33),
        SLOT9(34),
        SLOT10(35),
        SLOT11(36),
        SLOT12(37),
        SLOT13(38),
        SLOT14(39),
        SLOT15(40),
        SLOT16(41);

        private final int slotId;

        GodRecipeSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

}
