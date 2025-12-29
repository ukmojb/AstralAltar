package com.wdcftgg.astralaltar.crafting;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.RecipeAdapter;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class AddedAbstractAltarRecipe{

    private AddedAbstractAltarRecipe specialEffectRecovery = null;

    private int experiencePerCraft = 5, passiveStarlightRequirement;
    private final TileGodAltar.AltarLevel neededLevel;
    private final AccessibleRecipe recipe;
    private ItemStack out;

    private final AbstractAltarRecipe abstractAltarRecipe;

    private int uniqueRecipeId = -1;

    public AddedAbstractAltarRecipe(TileGodAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        this.neededLevel = neededLevel;
        this.recipe = recipe;
        this.out = recipe.getRecipeOutput();
        this.abstractAltarRecipe = null;
    }

    public AddedAbstractAltarRecipe(AbstractAltarRecipe recipe1) {
        this.abstractAltarRecipe = recipe1;
        this.neededLevel = TileGodAltar.AltarLevel.GOD_CRAFT;
        this.out = recipe1.getOutputForMatching();
        this.recipe = recipe1.getNativeRecipe();
    }

    public final void updateUniqueId(int id) {
        this.uniqueRecipeId = id;
    }

    public final int getUniqueRecipeId() {
        return uniqueRecipeId;
    }

    public AbstractAltarRecipe getAbstractAltarRecipe() {
        return abstractAltarRecipe;
    }

    public final void setSpecialEffectRecovery(AddedAbstractAltarRecipe specialEffectRecovery) {
        this.specialEffectRecovery = specialEffectRecovery;
    }

    @Nullable
    protected AddedActiveCraftingTask getCurrentTask(TileGodAltar ta) {
        return ta.getAddedActiveCraftingTask();
    }

    //Output used for rendering purposes. (Shouldn't be empty)
    @Nonnull
    public ItemStack getOutputForRender() {
        return ItemUtils.copyStackWithSize(out, out.getCount());
    }

    public AccessibleRecipe getNativeRecipe() {
        return recipe;
    }

    //Output that's dropped into the world (may be empty)
    @Nonnull
    public ItemStack getOutput(ShapeMap centralGridMap, TileGodAltar altar) {
        return ItemUtils.copyStackWithSize(out, out.getCount());
    }

    //Output that's used to search for the recipe and match/recognize special outputs.
    //PLEASE never return an empty stack as the recipe becomes unrecognisable by the mod!
    @Nonnull
    public ItemStack getOutputForMatching() {
        return ItemUtils.copyStackWithSize(out, out.getCount());
    }

    //Instead of calling this directly, call it via TileGodAltar.doesRecipeMatch() since that is more sensitive for the altar.
    public boolean matches(TileGodAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        if(!ignoreStarlightRequirement && !fulfillesStarlightRequirement(altar)) return false;

        if(this instanceof IGatedRecipe) {
            if(altar.getWorld().isRemote) {
                if(!((IGatedRecipe) this).hasProgressionClient()) return false;
            }
        }

        if(this instanceof INighttimeRecipe) {
            if(!ConstellationSkyHandler.getInstance().isNight(altar.getWorld())) return false;
        }

        int slotsContainRecipe = this.getNeededLevel().getAccessibleInventorySize();
        for (int slotId = 0; slotId < invHandler.getSlots(); slotId++) {
            if (slotId < slotsContainRecipe) continue;

            if (!invHandler.getStackInSlot(slotId).isEmpty()) {
                return false; // ItemStacks outside of the required slots for the recipe must be empty.
            }
        }

        ItemStack[] altarInv = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            altarInv[i] = invHandler.getStackInSlot(i);
        }
        RecipeAdapter adapter = new RecipeAdapter(altar.getCraftingRecipeWidth(), altar.getCraftingRecipeHeight());
        adapter.fill(altarInv);
        return recipe.matches(adapter, altar.getWorld());
    }

    public boolean fulfillesStarlightRequirement(TileGodAltar altar) {
        return altar.getStarlightStored() >= getPassiveStarlightRequired();
    }

    public AddedAbstractAltarRecipe setPassiveStarlightRequirement(int starlightRequirement) {
        this.passiveStarlightRequirement = starlightRequirement;
        return this;
    }

    public int getPassiveStarlightRequired() {
        return passiveStarlightRequirement;
    }

    public AddedAbstractAltarRecipe setCraftExperience(int exp) {
        this.experiencePerCraft = exp;
        return this;
    }

    public boolean allowsForChaining() {
        return true;
    }

    public int getCraftExperience() {
        return experiencePerCraft;
    }

    public float getCraftExperienceMultiplier() {
        return 1F;
    }

    public TileGodAltar.AltarLevel getNeededLevel() {
        return neededLevel;
    }

    public int craftingTickTime() {
        if (this.abstractAltarRecipe != null) {
            return this.abstractAltarRecipe.craftingTickTime();
        }
        return 100;
    }

    public void handleInputConsumption(TileGodAltar ta, AddedActiveCraftingTask craftingTask, ItemStackHandler inventory) {
        if (this.abstractAltarRecipe != null) {

            // ActiveCraftingTask task = new ActiveCraftingTask(abstractAltarRecipe, abstractAltarRecipe.craftingTickTime(), craftingTask.getPlayerCraftingUUID());
            // if (abstractAltarRecipe instanceof TraitRecipe) {
            //     TraitRecipe traitRecipe = (TraitRecipe) abstractAltarRecipe;
            //     traitRecipe.handleInputConsumption(ta, task, inventory);
            // } else if (abstractAltarRecipe instanceof ConstellationRecipe) {
            //     ConstellationRecipe constellationRecipe = (ConstellationRecipe) abstractAltarRecipe;
            //     constellationRecipe.handleInputConsumption(ta, task, inventory);
            // } else if (abstractAltarRecipe instanceof AttunementRecipe) {
            //     AttunementRecipe attunementRecipe = (AttunementRecipe) abstractAltarRecipe;
            //     attunementRecipe.handleInputConsumption(ta, task, inventory);
            // } else if (abstractAltarRecipe instanceof DiscoveryRecipe) {
            //     DiscoveryRecipe discoveryRecipe = (DiscoveryRecipe) abstractAltarRecipe;
            //     discoveryRecipe.handleInputConsumption(ta, task, inventory);
            // } else 
            // {
//                abstractAltarRecipe.handleInputConsumption(ta, task, inventory);

            // }

//            abstractAltarRecipe.handleInputConsumption(ta, new ActiveCraftingTask(abstractAltarRecipe, abstractAltarRecipe.craftingTickTime(), craftingTask.getPlayerCraftingUUID()), inventory);


        }
    }

    //Return false and the item in the slot is not consumed.
    public boolean mayDecrement(TileGodAltar ta, ShapedRecipeSlot slot) {
        return !requiresSpecialConsumption(recipe.getExpectedStackHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotID()));
    }

    public boolean mayDecrement(TileGodAltar ta, AttunementRecipe.AttunementAltarSlot slot) {
        if(!(abstractAltarRecipe instanceof AttunementRecipe)) return true;
        AttunementRecipe thisRecipe = (AttunementRecipe) abstractAltarRecipe;
        return !requiresSpecialConsumption(thisRecipe.getAttItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    public boolean mayDecrement(TileGodAltar ta, ConstellationRecipe.ConstellationAtlarSlot slot) {
        if(!(abstractAltarRecipe instanceof ConstellationRecipe)) return true;
        ConstellationRecipe thisRecipe = (ConstellationRecipe) abstractAltarRecipe;
        return !requiresSpecialConsumption(thisRecipe.getCstItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    public boolean mayDecrement(TileGodAltar ta, TraitRecipe.TraitRecipeSlot slot) {
        if(!(abstractAltarRecipe instanceof TraitRecipe)) return true;
        TraitRecipe thisRecipe = (TraitRecipe) abstractAltarRecipe;
        return !requiresSpecialConsumption(thisRecipe.getInnerTraitItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    public boolean mayDecrement(TileGodAltar ta, GodRecipe.GodRecipeSlot slot) {
        if(!(this instanceof GodRecipe)) return true;
        GodRecipe thisRecipe = (GodRecipe) this;
        return !requiresSpecialConsumption(thisRecipe.getInnerGodItemHandle(slot),
                ta.getInventoryHandler().getStackInSlot(slot.getSlotId()));
    }

    protected boolean requiresSpecialConsumption(ItemHandle handle, ItemStack stack) {
        return handle != null && !stack.isEmpty() &&
                (!ForgeHooks.getContainerItem(stack).isEmpty() ||
                        (handle.handleType == ItemHandle.Type.FLUID && FluidUtil.getFluidContained(stack) != null));
    }

    //Called if the respective method above returns 'false' to allow for proper decrement-handling.
    public void handleItemConsumption(TileGodAltar ta, ShapedRecipeSlot slot) {
        ItemHandle handle = recipe.getExpectedStackHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotID(), handle);
    }

    public void handleItemConsumption(TileGodAltar ta, AttunementRecipe.AttunementAltarSlot slot) {
        if(!(abstractAltarRecipe instanceof AttunementRecipe)) return;
        AttunementRecipe thisRecipe = (AttunementRecipe) abstractAltarRecipe;
        ItemHandle handle = thisRecipe.getAttItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    public void handleItemConsumption(TileGodAltar ta, ConstellationRecipe.ConstellationAtlarSlot slot) {
        if(!(abstractAltarRecipe instanceof ConstellationRecipe)) return;
        ConstellationRecipe thisRecipe = (ConstellationRecipe) abstractAltarRecipe;
        ItemHandle handle = thisRecipe.getCstItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    public void handleItemConsumption(TileGodAltar ta, TraitRecipe.TraitRecipeSlot slot) {
        if(!(abstractAltarRecipe instanceof TraitRecipe)) return;
        TraitRecipe thisRecipe = (TraitRecipe) abstractAltarRecipe;
        ItemHandle handle = thisRecipe.getInnerTraitItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    public void handleItemConsumption(TileGodAltar ta, GodRecipe.GodRecipeSlot slot) {
        if(!(this instanceof GodRecipe)) return;
        GodRecipe thisRecipe = (GodRecipe) this;
        ItemHandle handle = thisRecipe.getInnerGodItemHandle(slot);
        if(handle == null) return;

        consumeAndSetResult(ta.getInventoryHandler(), slot.getSlotId(), handle);
    }

    protected void consumeAndSetResult(IItemHandlerModifiable inv, int slot, ItemHandle handle) {
        ItemStack stack = inv.getStackInSlot(slot);
        if(!stack.isEmpty()) {
            FluidStack fs = FluidUtil.getFluidContained(stack);
            if(fs != null && handle.handleType == ItemHandle.Type.FLUID) {
                FluidActionResult fas = ItemUtils.drainFluidFromItem(stack, handle.getFluidTypeAndAmount(), true);
                if(fas.isSuccess()) {
                    inv.setStackInSlot(slot, fas.getResult());
                }
            } else {
                inv.setStackInSlot(slot, ForgeHooks.getContainerItem(stack));
            }
        }
    }

    protected static ShapedRecipe.Builder shapedRecipe(String basicName, Item out) {
        return ShapedRecipe.Builder.newShapedRecipe("internal/altar/" + basicName, out);
    }

    protected static ShapedRecipe.Builder shapedRecipe(String basicName, Block out) {
        return ShapedRecipe.Builder.newShapedRecipe("internal/altar/" + basicName, out);
    }

    protected static ShapedRecipe.Builder shapedRecipe(String basicName, ItemStack out) {
        return ShapedRecipe.Builder.newShapedRecipe("internal/altar/" + basicName, out);
    }

    //Can be used to applyServer modifications to items on the shapeMap.
    public void applyOutputModificationsServer(TileGodAltar ta, Random rand) {}

    public void onCraftServerFinish(TileGodAltar altar, Random rand) {}

    public void onCraftServerTick(TileGodAltar altar, AddedActiveCraftingTask.CraftingState state, int tick, int totalCraftingTime, Random rand) {}

    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileGodAltar altar, AddedActiveCraftingTask.CraftingState state, long tick, Random rand) {
        if(specialEffectRecovery != null) {
            try {
                specialEffectRecovery.onCraftClientTick(altar, state, tick, rand);
            } catch (Exception ignored) {}
        }
    }

    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileGodAltar te, double x, double y, double z, float partialTicks) {
        if(specialEffectRecovery != null) {
            try {
                specialEffectRecovery.onCraftTESRRender(te, x, y, z, partialTicks);
            } catch (Exception ignored) {}
        }
    }

}
