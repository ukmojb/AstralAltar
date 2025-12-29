package com.wdcftgg.astralaltar.mixin;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TraitRecipe.class)
public class MixinTraitRecipe {

//    @Shadow(remap = false)
//    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();
//
//    @Inject(method = "consumeOuterInputs",
//            at = @At(value = "HEAD", target = "Lhellfirepvp/astralsorcery/common/crafting/altar/recipes/TraitRecipe;consumeOuterInputs(Lhellfirepvp/astralsorcery/common/tile/TileAltar;Lhellfirepvp/astralsorcery/common/crafting/altar/ActiveCraftingTask;)V")
//            ,remap = false
//    )
////    /**
////     * @author 1
////     * @reason 1
////     */
////    @Overwrite
//    public void consumeOuterInputs(TileAltar altar, ActiveCraftingTask craftingTask, CallbackInfo ci) {
//        List<GodRecipe.CraftingFocusStack> stacks = GodRecipe.collectCurrentStacks(craftingTask.getCraftingData());
//        System.out.println("stacks--" + stacks.size());
//        for (GodRecipe.CraftingFocusStack stack : stacks) {
//            if(stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; //Duh
//
//            ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
//            TileAttunementRelay tar = MiscUtils.getTileAt(altar.getWorld(), altar.getPos().add(stack.offset), TileAttunementRelay.class, true);
//            if(tar != null) {
//                //We take a leap of faith and assume the required matches the found itemstack in terms of crafting matching
//                //It should match since we literally check in the same tick as we finish the recipe if it's valid...
//                ItemStack found = tar.getInventoryHandler().getStackInSlot(0);
//                if(required.getFluidTypeAndAmount() != null) {
//                    if (!found.isEmpty()) {
//                        FluidActionResult fas = ItemUtils.drainFluidFromItem(found, required.getFluidTypeAndAmount(), true);
//                        if (fas.isSuccess()) {
//                            tar.getInventoryHandler().setStackInSlot(0, fas.getResult());
//                            tar.markForUpdate();
//                        }
//                    }
//                } else if(!ForgeHooks.getContainerItem(found).isEmpty()) {
//                    tar.getInventoryHandler().setStackInSlot(0, ForgeHooks.getContainerItem(found));
//                    tar.markForUpdate();
//                } else {
//                    ItemUtils.decrStackInInventory(tar.getInventoryHandler(), 0);
//                    tar.markForUpdate();
//                }
//            }
//        }
//    }

}
