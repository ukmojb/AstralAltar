package com.wdcftgg.astralaltar.mixin.block;

import com.wdcftgg.astralaltar.blocks.ModBlocks;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileAltar.class, remap = false)
public abstract class MixinTileAltar extends TileEntity {

    @Shadow private ActiveCraftingTask craftingTask;
    @Shadow private int starlightStored;

    @Shadow public abstract TileAltar.AltarLevel getAltarLevel();

    @Shadow public abstract ItemStack getFocusItem();

    @Redirect(
            method = "finishCrafting",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/astralsorcery/common/util/ItemUtils;dropItem(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/item/EntityItem;"
            )
    )
    private EntityItem astralaltar$skipDroppedGodAltarResult(World world, double x, double y, double z, ItemStack stack) {
        if (this.astralaltar$shouldUpgradeToGodAltar(stack)) {
            return new EntityItem(world, x, y, z, stack.copy());
        }
        return ItemUtils.dropItem(world, x, y, z, stack);
    }

    @Inject(
            method = "finishCrafting",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/astralsorcery/common/data/research/ResearchManager;informCraftingAltarCompletion(Lhellfirepvp/astralsorcery/common/tile/TileAltar;Lhellfirepvp/astralsorcery/common/crafting/altar/ActiveCraftingTask;)V"
            )
    )
    private void astralaltar$upgradeTraitAltarToGodAltar(CallbackInfo ci) {
        if (this.craftingTask == null || this.getAltarLevel() != TileAltar.AltarLevel.TRAIT_CRAFT) {
            return;
        }

        AbstractAltarRecipe recipe = this.craftingTask.getRecipeToCraft();
        ItemStack match = recipe.getOutputForMatching();
        if (!this.astralaltar$shouldUpgradeToGodAltar(match)) {
            return;
        }

        TileReceiverBaseInventory.ItemHandlerTile inventory = ((TileReceiverBaseInventory) (Object) this).getInventoryHandler();
        ItemStack[] preserved = new ItemStack[Math.min(25, inventory.getSlots())];
        for (int i = 0; i < preserved.length; i++) {
            preserved[i] = inventory.getStackInSlot(i).copy();
        }

        ItemStack focus = this.getFocusItem().copy();
        int storedStarlight = this.starlightStored;
        World world = this.getWorld();
        BlockPos pos = this.getPos();
        if (!world.setBlockState(pos, ModBlocks.godAltar.getDefaultState())) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileGodAltar)) {
            return;
        }

        TileGodAltar godAltar = (TileGodAltar) tile;
        for (int i = 0; i < preserved.length; i++) {
            if (!preserved[i].isEmpty()) {
                godAltar.getInventoryHandler().setStackInSlot(i, preserved[i]);
            }
        }
        if (!focus.isEmpty()) {
            godAltar.setFocusStack(focus);
        }
        if (storedStarlight > 0) {
            godAltar.receiveStarlight(null, storedStarlight / 200.0D);
        }
        godAltar.markForUpdate();
    }

    @Unique
    private boolean astralaltar$shouldUpgradeToGodAltar(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() == Item.getItemFromBlock(ModBlocks.godAltar)
                && this.getAltarLevel() == TileAltar.AltarLevel.TRAIT_CRAFT;
    }
}
