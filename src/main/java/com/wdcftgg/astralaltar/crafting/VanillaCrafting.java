package com.wdcftgg.astralaltar.crafting;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/7/16 21:50
 */

import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.blocks.ModBlocks;
import com.wdcftgg.astralaltar.items.ModItems;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class VanillaCrafting
{

    private static TIntSet usedHashes = new TIntHashSet();
    public static void init() {
        initVanillaRecipes();
    }

    public static void initVanillaRecipes() {
        addShapedRecipe(new ItemStack(ModBlocks.starMetalBlock), new Object[]{"AAA", "AAA", "AAA", 'A', ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack()});


    }

    public static void addShapedRecipe(ItemStack output, Object... params) {
        GameRegistry.addShapedRecipe(new ResourceLocation(AstralAltar.MODID, getName(output.getItem())), (ResourceLocation)null, output, params);
    }

    public static void addRecipe(IRecipe recipe, String id) {
        ForgeRegistries.RECIPES.register(recipe.setRegistryName(new ResourceLocation(AstralAltar.MODID, id)));
    }

    private static String getName(Item item) {
        int hash = item.getRegistryName().hashCode();
        usedHashes.add(hash);
        return AstralAltar.MODID + "_" + hash;
    }
}
