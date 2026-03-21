package com.wdcftgg.astralaltar.crafting.recipe;

import hellfirepvp.astralsorcery.common.block.BlockInfusedWood;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EvolutionSextantUpgradeRecipe extends TraitRecipe {

    private static ItemStack getAdvancedSextant() {
        ItemStack sextant = ItemsAS.sextant.getDefaultInstance();
        ItemSextant.setAdvanced(sextant);
        return sextant;
    }

    public EvolutionSextantUpgradeRecipe() {
        super(ShapedRecipe.Builder.newShapedRecipe("astralaltar/internal/altar/sextant/upgrade/evolution", ItemsAS.sextant)
                .addPart(getAdvancedSextant(),
                        ShapedRecipeSlot.CENTER)
                .addPart(BlockInfusedWood.WoodType.ENRICHED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemColoredLens.ColorType.SPECTRAL.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(ItemsAS.useableDust,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        setAttItem(ItemCraftingComponent.MetaType.STARDUST.asStack(),
                AttunementAltarSlot.LOWER_LEFT,
                AttunementAltarSlot.LOWER_RIGHT);
        setCstItem(ItemsAS.infusedGlass,
                ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationAtlarSlot.UP_LEFT_LEFT);
        setInnerTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                TraitRecipeSlot.UPPER_CENTER);
        addOuterTraitItem(ItemCraftingComponent.MetaType.GLASS_LENS.asStack());
        addOuterTraitItem(ItemColoredLens.ColorType.SPECTRAL.asStack());
        addOuterTraitItem(ItemCraftingComponent.MetaType.GLASS_LENS.asStack());
    }



    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        return new ItemStack(ItemsAS.sextant);
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack adv = new ItemStack(ItemsAS.sextant);
        ItemSextant.setAdvanced(adv);
        setEvolution(adv);
        return adv;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack sextant = altar.getInventoryHandler().getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        sextant = ItemUtils.copyStackWithSize(sextant, sextant.getCount());
        ItemSextant.setAdvanced(sextant);
        setEvolution(sextant);
        return sextant;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        ItemStack sextant = invHandler.getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if(!ItemSextant.isAdvanced(sextant)) {
            return false;
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    public void setEvolution(ItemStack sextantStack) {
        if (!sextantStack.isEmpty() && sextantStack.getItem() instanceof ItemSextant) {
            NBTHelper.getPersistentData(sextantStack).setBoolean("evolution", true);
        }
    }
}
