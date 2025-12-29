package com.wdcftgg.astralaltar.crafting;

import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockInfusedWood;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static com.wdcftgg.astralaltar.crafting.AddedAltarRecipeRegistry.registerGodRecipe;
import static hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;

public class AddedRegistryRecipes {

    public static GodRecipe test;

    public static void initAstralRecipes() {
        initGodRecipes();
    }
    public static void initGodRecipes() {
        test = registerGodRecipe(newShapedRecipe("internal/altar/bore_core111", Items.APPLE.getDefaultInstance())
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
                GodRecipe.AttunementAltarSlot.UPPER_RIGHT,
                GodRecipe.AttunementAltarSlot.UPPER_LEFT);
        test.setCstItem(BlockInfusedWood.WoodType.ENRICHED.asStack(),
                GodRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
                GodRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        test.setInnerTraitItem(BlockInfusedWood.WoodType.ENRICHED.asStack(),
                GodRecipe.TraitRecipeSlot.LEFT_CENTER,
                GodRecipe.TraitRecipeSlot.RIGHT_CENTER);
        test.setInnerTraitItem(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                GodRecipe.TraitRecipeSlot.UPPER_CENTER);
        test.setCstItem(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                GodRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
                GodRecipe.ConstellationAtlarSlot.UP_UP_RIGHT);
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        test.addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        test.setPassiveStarlightRequirement(6500);
    }
}
