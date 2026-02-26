package com.wdcftgg.astralaltar.crafting;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.recipe.GodRecipe;
import hellfirepvp.astralsorcery.common.crafting.ISpecialCraftingEffects;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemComparator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AddedAltarRecipeRegistry {

    private static Map<ItemStack, ISpecialCraftingEffects> effectRecoveryMap = new HashMap<>();

    public static Map<TileGodAltar.AltarLevel, List<AddedAbstractAltarRecipe>> mtRecipes = new HashMap<>();
    public static Map<TileGodAltar.AltarLevel, List<AddedAbstractAltarRecipe>> recipes = new HashMap<>();
    private static AddedAbstractAltarRecipe[] compiledRecipeArray = null;

    private static Map<TileGodAltar.AltarLevel, List<AddedAbstractAltarRecipe>> localFallbackCache = new HashMap<>();

    //NEVER call this. this should only get called once at post init to compile all recipes for fast access.
    //After this is called, changes to recipe registry might break stuff.
    public static void compileRecipes() {

        for (TileAltar.AltarLevel level : AltarRecipeRegistry.recipes.keySet()) {
            List<AddedAbstractAltarRecipe> newList = AltarRecipeRegistry.recipes.get(level).stream().map(AddedAbstractAltarRecipe::new).collect(Collectors.toList());
            recipes.get(TileGodAltar.AltarLevel.values()[level.ordinal()]).addAll(newList);
        }

        for (TileAltar.AltarLevel level : AltarRecipeRegistry.mtRecipes.keySet()) {
            List<AddedAbstractAltarRecipe> newList = AltarRecipeRegistry.mtRecipes.get(level).stream().map(AddedAbstractAltarRecipe::new).collect(Collectors.toList());
            mtRecipes.get(TileGodAltar.AltarLevel.values()[level.ordinal()]).addAll(newList);
        }

        compiledRecipeArray = null;

        int totalNeeded = 0;
        for (TileGodAltar.AltarLevel level : recipes.keySet()) {
            totalNeeded += recipes.get(level).size();
        }
        for (TileGodAltar.AltarLevel level : mtRecipes.keySet()) {
            totalNeeded += mtRecipes.get(level).size();
        }

        int i = 0;
        compiledRecipeArray = new AddedAbstractAltarRecipe[totalNeeded];
        for (TileGodAltar.AltarLevel l : TileGodAltar.AltarLevel.values()) {
            List<AddedAbstractAltarRecipe> recipeList = recipes.get(l);

            for (AddedAbstractAltarRecipe rec : recipeList) {
//                if (l == TileGodAltar.AltarLevel.GOD_CRAFT) System.out.println(rec.getUniqueRecipeId());
                compiledRecipeArray[i] = rec;
                rec.updateUniqueId(i);
                i++;
            }
            recipeList = mtRecipes.get(l);
            for (AddedAbstractAltarRecipe rec : recipeList) {
                compiledRecipeArray[i] = rec;
                rec.updateUniqueId(i);
                i++;
            }
        }
    }

    public static void cacheLocalRecipes() {
        if(localFallbackCache.isEmpty()) {
            for (TileGodAltar.AltarLevel al : TileGodAltar.AltarLevel.values()) {
                localFallbackCache.put(al, new LinkedList<>());
                localFallbackCache.get(al).addAll(recipes.get(al));
            }
        }
    }

    public static void loadFromFallback() {
        if(!localFallbackCache.isEmpty()) {
            for (TileGodAltar.AltarLevel al : TileGodAltar.AltarLevel.values()) {
                recipes.get(al).addAll(localFallbackCache.get(al));
            }
        }
    }

    @Nullable
    public static AddedAbstractAltarRecipe getRecipe(int id) {
        if(id < 0 || id >= compiledRecipeArray.length) {
            return null;
        }
        return compiledRecipeArray[id];
    }

    @Nullable
    public static AddedAbstractAltarRecipe getRecipeSlow(@Nullable ResourceLocation id) {
        if (id == null) {
            return null;
        }
        for (Collection<AddedAbstractAltarRecipe> recipeList : recipes.values()) {
            for (AddedAbstractAltarRecipe recipe : recipeList) {
                if (recipe.getNativeRecipe().getRegistryName().equals(id)) {
                    return recipe;
                }
            }
        }
        for (Collection<AddedAbstractAltarRecipe> recipeList : mtRecipes.values()) {
            for (AddedAbstractAltarRecipe recipe : recipeList) {
                if (recipe.getNativeRecipe().getRegistryName().equals(id)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    public static List<AddedAbstractAltarRecipe> getAltarRecipesByOutput(ItemStack output, TileGodAltar.AltarLevel altarLevel) {
        List<AddedAbstractAltarRecipe> list = new LinkedList<>();
        for (AddedAbstractAltarRecipe recipe : recipes.get(altarLevel)) {
            ItemStack out = recipe.getOutputForMatching();
            if (!out.isEmpty() && ItemComparator.compare(out, output, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                list.add(recipe);
            }
        }
        for (AddedAbstractAltarRecipe recipe : mtRecipes.get(altarLevel)) {
            ItemStack out = recipe.getOutputForMatching();
            if (!out.isEmpty() && ItemComparator.compare(out, output, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                list.add(recipe);
            }
        }
        return list;
    }

    /*
     * Returns the Recipe that was removed if successful.
     */
    @Nullable
    @Deprecated
    public static AddedAbstractAltarRecipe removeFindRecipeByOutputAndLevel(ItemStack output, TileGodAltar.AltarLevel altarLevel) {
        Iterator<AddedAbstractAltarRecipe> iterator = recipes.get(altarLevel).iterator();
        while (iterator.hasNext()) {
            AddedAbstractAltarRecipe rec = iterator.next();
            ItemStack out = rec.getOutputForMatching();
            if (!out.isEmpty() && ItemComparator.compare(rec.getOutputForMatching(), output, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                iterator.remove();
                return rec;
            }
        }
        return null;
    }

    /*
     * Returns the Recipe that was removed if successful.
     */
    @Nullable
    public static AddedAbstractAltarRecipe removeRecipeFromCache(@Nullable AddedAbstractAltarRecipe recipe) {
        if (recipe == null) {
            return null;
        }
        for (TileGodAltar.AltarLevel al : recipes.keySet()) {
            Iterator<AddedAbstractAltarRecipe> iterator = recipes.get(al).iterator();
            while (iterator.hasNext()) {
                AddedAbstractAltarRecipe regRecipe = iterator.next();
                if (regRecipe.getNativeRecipe().getRegistryName().equals(recipe.getNativeRecipe().getRegistryName())) {
                    iterator.remove();
                    return regRecipe;
                }
            }
        }
        return null;
    }

    public static GodRecipe registerGodRecipe(AccessibleRecipeAdapater recipe) {
        GodRecipe tr = new GodRecipe(recipe);
        registerAltarRecipe(tr);
        return tr;
    }

    public static <T extends AddedAbstractAltarRecipe> T registerAltarRecipe(T recipe) {
        TileGodAltar.AltarLevel level = recipe.getNeededLevel();
        recipes.get(level).add(recipe);
        if(recipe instanceof ISpecialCraftingEffects) {
            registerSpecialEffects(recipe);
        }
        if(AddedCraftingAccessManager.hasCompletedSetup()) {
            CraftingAccessManager.compile();
        }
        return recipe;
    }

    private static void registerSpecialEffects(AddedAbstractAltarRecipe ar) {
        ItemStack out = ar.getOutputForMatching();
        if(out.isEmpty()) return; //Well....

        boolean has = false;
        for (ItemStack i : effectRecoveryMap.keySet()) {
            if (ItemComparator.compare(out, i, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                has = true;
            }
        }
        if(!has) {
            effectRecoveryMap.put(out, (ISpecialCraftingEffects) ar);
        }
    }

    //null === false
    @Nullable
    public static ISpecialCraftingEffects shouldHaveSpecialEffects(AddedAbstractAltarRecipe ar) {
        if(ar == null || ar instanceof ISpecialCraftingEffects) return null;
        ItemStack match = ar.getOutputForMatching();
        if(match.isEmpty()) return null;
        for (Map.Entry<ItemStack, ISpecialCraftingEffects> effectEntry : effectRecoveryMap.entrySet()) {
            if(effectEntry.getValue().needsStrictMatching() ?
                    ItemComparator.compare(match, effectEntry.getKey(), ItemComparator.Clause.Sets.ITEMSTACK_STRICT) :
                    ItemComparator.compare(match, effectEntry.getKey(), ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                return effectEntry.getValue();
            }
        }
        return null;
    }

    public static Collection<AddedAbstractAltarRecipe> getRecipesForLevel(TileGodAltar.AltarLevel al) {
        List<AddedAbstractAltarRecipe> cache = Lists.newLinkedList();
        cache.addAll(recipes.get(al));
        cache.addAll(mtRecipes.get(al));
        return cache;
    }

    @Nullable
    public static Object findMatchingRecipe(TileGodAltar ta, boolean ignoreStarlightRequirement) {
        TileGodAltar.AltarLevel lowestAllowed = ta.matchDownMultiblocks(ta.getGodAltarLevel());
        if (lowestAllowed == null) {
            lowestAllowed = TileGodAltar.AltarLevel.GOD_CRAFT;
        }
        for (int i = lowestAllowed.ordinal(); i >= 0; i--) {
            TileGodAltar.AltarLevel lvl = TileGodAltar.AltarLevel.values()[i];
            List<AddedAbstractAltarRecipe> validRecipes = recipes.get(lvl);
//            List<AbstractAltarRecipe> lowerValidRecipes = AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.TRAIT_CRAFT);
            if(!validRecipes.isEmpty()) {
                for (AddedAbstractAltarRecipe rec : validRecipes) {
                    if(ta.doesRecipeMatch(rec, ignoreStarlightRequirement)) {
                        return rec;
                    }
                }
            }
            validRecipes = mtRecipes.get(lvl);
            if(!validRecipes.isEmpty()) {
                for (AddedAbstractAltarRecipe rec : validRecipes) {
                    if(ta.doesRecipeMatch(rec, ignoreStarlightRequirement)) {
                        return rec;
                    }
                }
            }
        }

//        for (int i = TileAltar.AltarLevel.TRAIT_CRAFT.ordinal(); i >= 0; i--) {
//            TileAltar.AltarLevel lvl = TileAltar.AltarLevel.values()[i];
//            List<AbstractAltarRecipe> lowerValidRecipes = AltarRecipeRegistry.recipes.get(lvl);
//
//            if(lowerValidRecipes != null) {
//                for (AbstractAltarRecipe rec : lowerValidRecipes) {
//                    if(ta.doesRecipeMatch(rec, ignoreStarlightRequirement)) {
//                        return rec;
//                    }
//                }
//            }
//            lowerValidRecipes = AltarRecipeRegistry.mtRecipes.get(lvl);
//            if(lowerValidRecipes != null) {
//                for (AbstractAltarRecipe rec : lowerValidRecipes) {
//                    if(ta.doesRecipeMatch(rec, ignoreStarlightRequirement)) {
//                        return rec;
//                    }
//                }
//            }
//        }

        return null;
        /*List<TileGodAltar.AltarLevel> levels = new ArrayList<>();
        List<AddedAbstractAltarRecipe> validRecipes = new LinkedList<>();
        for (int i = 0; i < level.ordinal() + 1; i++) {
            levels.add(TileGodAltar.AltarLevel.values()[i]);
        }
        for (TileGodAltar.AltarLevel valid : levels) {
            validRecipes.addAll(recipes.get(valid));
        }
        for (AddedAbstractAltarRecipe recipe : validRecipes) {
            if(recipe.matches(ta)) {
                return recipe;
            }
        }*/
    }

    static {
        for (TileGodAltar.AltarLevel al : TileGodAltar.AltarLevel.values()) {
            recipes.put(al, new LinkedList<>());
            mtRecipes.put(al, new LinkedList<>());
        }
    }

}
