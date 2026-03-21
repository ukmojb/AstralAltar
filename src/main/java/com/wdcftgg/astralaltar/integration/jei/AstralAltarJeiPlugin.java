package com.wdcftgg.astralaltar.integration.jei;

import com.wdcftgg.astralaltar.blocks.ModBlocks;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.crafting.AddedAbstractAltarRecipe;
import com.wdcftgg.astralaltar.crafting.AddedAltarRecipeRegistry;
import com.wdcftgg.astralaltar.crafting.altar.GodRecipe;
import com.wdcftgg.astralaltar.gui.GuiAltarGod;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.startup.StackHelper;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class AstralAltarJeiPlugin implements IModPlugin {

    public static final String ID_ALTAR_GOD = "astralaltar.altar.god";

    private ISubtypeRegistry subtypeRegistry;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        this.subtypeRegistry = subtypeRegistry;
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new IRecipeCategory[]{new GodAltarRecipeCategory(guiHelper)});
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(GodRecipe.class, GodAltarRecipeWrapper::new, ID_ALTAR_GOD);
        registry.addRecipes(getGodRecipes(), ID_ALTAR_GOD);

        ItemStack godAltarStack = new ItemStack(ModBlocks.godAltar);
        registry.addRecipeCatalyst(godAltarStack, new String[]{ID_ALTAR_GOD});

        registry.addRecipeClickArea(
                GuiAltarGod.class,
                223,
                45,
                48,
                48,
                ModIntegrationJEI.idAltarDiscovery,
                ModIntegrationJEI.idAltarAttunement,
                ModIntegrationJEI.idAltarConstellation,
                ModIntegrationJEI.idAltarTrait,
                ID_ALTAR_GOD
        );

        if (subtypeRegistry == null) {
            return;
        }

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
        IRecipeTransferHandlerHelper transferHelper = jeiHelpers.recipeTransferHandlerHelper();
        StackHelper jeiStackHelper = new StackHelper(subtypeRegistry);

        transferRegistry.addRecipeTransferHandler(
                new GodAltarRecipeTransferHandler(jeiStackHelper, transferHelper),
                ModIntegrationJEI.idAltarDiscovery
        );
        transferRegistry.addRecipeTransferHandler(
                new GodAltarRecipeTransferHandler(jeiStackHelper, transferHelper),
                ModIntegrationJEI.idAltarAttunement
        );
        transferRegistry.addRecipeTransferHandler(
                new GodAltarRecipeTransferHandler(jeiStackHelper, transferHelper),
                ModIntegrationJEI.idAltarConstellation
        );
        transferRegistry.addRecipeTransferHandler(
                new GodAltarRecipeTransferHandler(jeiStackHelper, transferHelper),
                ModIntegrationJEI.idAltarTrait
        );
        transferRegistry.addRecipeTransferHandler(
                new GodAltarRecipeTransferHandler(jeiStackHelper, transferHelper),
                ID_ALTAR_GOD
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }

    private static List<GodRecipe> getGodRecipes() {
        Collection<AddedAbstractAltarRecipe> recipes = AddedAltarRecipeRegistry.getRecipesForLevel(TileGodAltar.AltarLevel.GOD_CRAFT);
        return recipes.stream()
                .filter(GodRecipe.class::isInstance)
                .map(GodRecipe.class::cast)
                .collect(Collectors.toList());
    }
}
