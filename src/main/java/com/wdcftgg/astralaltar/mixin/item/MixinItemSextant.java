package com.wdcftgg.astralaltar.mixin.item;

import com.wdcftgg.astralaltar.cilent.effect.NewEffectHandler;
import com.wdcftgg.astralaltar.init.multiblock.NewStructureRegistry;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemSextant.class, remap = false)
public class MixinItemSextant {


    @Inject(method = "onRightClick",
            at = @At(value = "HEAD", target = "Lhellfirepvp/astralsorcery/common/item/tool/sextant/ItemSextant;onRightClick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/ItemStack;)Z"),
            cancellable = true)
    public void onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        TileEntity te = world.getTileEntity(pos);
        if(te != null && NewStructureRegistry.hasTileEntity(te)) {
            PatternBlockArray struct = NewStructureRegistry.getPatternBlockArray(te);
            if (struct != null) {
                if (!struct.matches(world, pos)) {
                    if(!world.isRemote && world instanceof WorldServer &&
                            entityPlayer.isCreative() && entityPlayer.isSneaking() &&
                            MiscUtils.isChunkLoaded(world, pos)) {
                        IBlockState current = world.getBlockState(pos);
                        struct.placeInWorld(world, pos);
                        if(!world.getBlockState(pos).equals(current)) {
                            world.setBlockState(pos, current);
                        }
                    }
                    if(world.isRemote) {
                        NewEffectHandler.getInstance().requestStructurePreviewFor(te);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "needsSpecialHandling",
            at = @At(value = "HEAD", target = "Lhellfirepvp/astralsorcery/common/item/tool/sextant/ItemSextant;needsSpecialHandling(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z"),
            cancellable = true)
    public void needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        TileEntity te = world.getTileEntity(at);
        if(te != null && NewStructureRegistry.hasTileEntity(te)) {
            PatternBlockArray struct = NewStructureRegistry.getPatternBlockArray(te);
            cir.setReturnValue(struct != null);
        }
    }
}
