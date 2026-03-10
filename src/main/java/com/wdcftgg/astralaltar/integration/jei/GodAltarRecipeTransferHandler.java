package com.wdcftgg.astralaltar.integration.jei;

import com.google.common.collect.Maps;
import com.wdcftgg.astralaltar.gui.container.ContainerAltarGod;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.util.JEISessionHandler;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GodAltarRecipeTransferHandler implements IRecipeTransferHandler<ContainerAltarGod> {

    private final StackHelper stackHelper;
    private final IRecipeTransferHandlerHelper handlerHelper;
    private final List<Integer> recipeSlotNumbers;

    public GodAltarRecipeTransferHandler(StackHelper stackHelper,
                                         IRecipeTransferHandlerHelper handlerHelper,
                                         List<Integer> recipeSlotNumbers) {
        this.stackHelper = stackHelper;
        this.handlerHelper = handlerHelper;
        this.recipeSlotNumbers = Collections.unmodifiableList(new ArrayList<>(recipeSlotNumbers));
    }

    @Override
    public Class<ContainerAltarGod> getContainerClass() {
        return ContainerAltarGod.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(ContainerAltarGod container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (!JEISessionHandler.getInstance().isJeiOnServer()) {
            return handlerHelper.createUserErrorWithTooltip(Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server"));
        }

        Map<Integer, Slot> playerSlots = new HashMap<>();
        for (Slot slot : container.inventorySlots.subList(0, 36)) {
            playerSlots.put(slot.slotNumber, slot);
        }

        Map<Integer, Slot> recipeSlots = new HashMap<>();
        for (Integer slotNumber : recipeSlotNumbers) {
            if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
                Log.get().error("Recipe Transfer helper {} references slot {} outside of the inventory's size {}",
                        container.getClass(), slotNumber, container.inventorySlots.size());
                return handlerHelper.createInternalError();
            }
            Slot slot = container.getSlot(slotNumber);
            recipeSlots.put(slot.slotNumber, slot);
        }

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = Maps.newHashMap(itemStacks.getGuiIngredients());
        List<Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>>> orderedInputs = new ArrayList<>();
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
            Integer ingredientIndex = entry.getKey();
            IGuiIngredient<ItemStack> ingredient = entry.getValue();
            if (ingredientIndex == null || ingredientIndex <= 0 || ingredientIndex > recipeSlotNumbers.size()) {
                continue;
            }
            if (!ingredient.isInput() || ingredient.getAllIngredients().isEmpty()) {
                continue;
            }
            orderedInputs.add(entry);
        }
        orderedInputs.sort(Comparator.comparingInt(Map.Entry::getKey));

        Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
        int filledRecipeSlots = 0;
        for (Slot slot : recipeSlots.values()) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                if (!slot.canTakeStack(player)) {
                    Log.get().error("Recipe Transfer helper {} does not work for container {}. Player can't move item out of Crafting Slot number {}",
                            this.getClass(), container.getClass(), slot.slotNumber);
                    return handlerHelper.createInternalError();
                }
                filledRecipeSlots++;
                availableItemStacks.put(slot.slotNumber, stack.copy());
            }
        }

        int emptyPlayerSlots = 0;
        for (Slot slot : playerSlots.values()) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) {
                emptyPlayerSlots++;
            } else {
                availableItemStacks.put(slot.slotNumber, stack.copy());
            }
        }

        if (filledRecipeSlots - orderedInputs.size() > emptyPlayerSlots) {
            return handlerHelper.createUserErrorWithTooltip(Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full"));
        }

        Map<Integer, IGuiIngredient<ItemStack>> indexedIngredients = new HashMap<>();
        for (int i = 0; i < orderedInputs.size(); i++) {
            indexedIngredients.put(i, orderedInputs.get(i).getValue());
        }

        StackHelper.MatchingItemsResult matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, indexedIngredients);
        if (!matchingItemsResult.missingItems.isEmpty()) {
            return handlerHelper.createUserErrorForSlots(
                    Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing"),
                    remapMissingGuiSlots(matchingItemsResult.missingItems, orderedInputs)
            );
        }

        if (doTransfer) {
            List<Integer> craftingSlotIndexes = new ArrayList<>(recipeSlotNumbers);
            List<Integer> inventorySlotIndexes = new ArrayList<>(playerSlots.keySet());
            Collections.sort(craftingSlotIndexes);
            Collections.sort(inventorySlotIndexes);
            PacketRecipeTransfer packet = new PacketRecipeTransfer(
                    Maps.newHashMap(matchingItemsResult.matchingItems),
                    craftingSlotIndexes,
                    inventorySlotIndexes,
                    maxTransfer,
                    true
            );
            JustEnoughItems.getProxy().sendPacketToServer(packet);
        }

        return null;
    }

    private static List<Integer> remapMissingGuiSlots(Collection<Integer> missingItems,
                                                      List<Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>>> orderedInputs) {
        Set<Integer> remapped = new LinkedHashSet<>();
        for (Integer missingIndex : missingItems) {
            if (missingIndex != null && missingIndex >= 0 && missingIndex < orderedInputs.size()) {
                remapped.add(orderedInputs.get(missingIndex).getKey());
            } else if (missingIndex != null) {
                remapped.add(missingIndex);
            }
        }
        return new ArrayList<>(remapped);
    }
}
