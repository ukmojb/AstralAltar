package com.wdcftgg.astralaltar.init;

import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe.GodRecipeSlot;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockInfusedWood;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe.AttunementAltarSlot;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe.ConstellationAtlarSlot;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe.TraitRecipeSlot;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import static com.wdcftgg.astralaltar.crafting.AddedAltarRecipeRegistry.registerGodRecipe;
import static hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;

public class AddedRegistryRecipes {

    public static GodRecipe test;

    public static void initAstralRecipes() {
        initGodRecipes();
    }
    public static void initGodRecipes() {
        test = registerGodRecipe(newShapedRecipe("internal/altar/bore_core111", Blocks.BIRCH_STAIRS)
                .addPart(Items.APPLE.getDefaultInstance(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        test.setAttItem(BlockInfusedWood.WoodType.ENRICHED.asStack(),
                AttunementAltarSlot.UPPER_RIGHT,
                AttunementAltarSlot.UPPER_LEFT);
        test.setCstItem(BlockInfusedWood.WoodType.ENRICHED.asStack(),
                ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        test.setInnerTraitItem(BlockInfusedWood.WoodType.ENRICHED.asStack(),
                TraitRecipeSlot.LEFT_CENTER,
                TraitRecipeSlot.RIGHT_CENTER);
        test.setInnerTraitItem(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                TraitRecipeSlot.UPPER_CENTER);
        test.setCstItem(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ConstellationAtlarSlot.UP_UP_LEFT,
                ConstellationAtlarSlot.UP_UP_RIGHT);
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        test.setGodItem(Items.BOOK,
                GodRecipeSlot.LOWER_RIGHT,
                GodRecipeSlot.LEFT_LOWER,
                GodRecipeSlot.RIGHT_UPPER);
        test.setPassiveStarlightRequirement(6500);
        test.setRequiredConstellation(Constellations.vicio);

    }
}
