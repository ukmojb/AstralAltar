package com.wdcftgg.astralaltar.proxy;

import com.wdcftgg.astralaltar.crafting.AddedCraftingAccessManager;
import com.wdcftgg.astralaltar.crafting.AddedRegistryRecipes;
import com.wdcftgg.astralaltar.gui.GuiElementLoader;
import com.wdcftgg.astralaltar.init.RegistryStructures;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;


public class CommonProxy{


	public void registerItemRenderer(Item item, int meta, String id) {
		//Ignored
	}

	public void onPreInit() {

		AddedRegistryRecipes.initAstralRecipes();
	}

	public void onPostInit() {

		AddedCraftingAccessManager.compile();
	}

	public void onInit(){
		RegistryStructures.init();


		new GuiElementLoader();
	}

	private AccessibleRecipeAdapater buildNativeRecipe(ItemHandle[] inputs, ItemStack out) {
		ShapedRecipe.Builder builder = newShapedRecipe("test", out);
		for (int i = 0; i < 9; i++) {
			ItemHandle itemHandle = inputs[i];
			if(itemHandle == null) continue;
			ShapedRecipeSlot srs = ShapedRecipeSlot.values()[i];
			builder.addPart(inputs[i], srs);
		}
		return builder.unregisteredAccessibleShapedRecipe();
	}

}
