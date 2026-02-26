package com.wdcftgg.astralaltar.mixin.block;

import com.wdcftgg.astralaltar.blocks.BlockGodAltar;
import com.wdcftgg.astralaltar.blocks.ModBlocks;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockAttunementRelay;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = BlockAttunementRelay.class, remap = false)
public class MixinBlockAttunementRelay {

    @Inject(method = "startSearchRelayLinkThreadAt",
            at = @At(value = "HEAD", target = "Lhellfirepvp/astralsorcery/common/block/BlockAttunementRelay;startSearchRelayLinkThreadAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)V")
    )
    private static void startSearchRelayLinkThreadAt(World world, BlockPos pos, boolean recUpdate, CallbackInfo ci) {
        Thread searchThread = new Thread(() -> {
            BlockPos closestAltar = null;
            double dstSqOtherRelay = Double.MAX_VALUE;
            BlockArray relaysAndAltars = BlockDiscoverer.searchForBlocksAround(world, pos, 16,
                    (world1, pos1, state1) -> state1.getBlock().equals(ModBlocks.godAltar) || state1.getBlock().equals(BlocksAS.attunementRelay));
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : relaysAndAltars.getPattern().entrySet()) {
                if(entry.getValue().type.equals(ModBlocks.godAltar)) {
                    if(closestAltar == null || pos.distanceSq(entry.getKey()) < pos.distanceSq(closestAltar)) {
                        closestAltar = entry.getKey();
                    }
                } else {
                    double dstSqOther = entry.getKey().distanceSq(pos);
                    if(dstSqOther < dstSqOtherRelay) {
                        dstSqOtherRelay = dstSqOther;
                    }
                }
            }

            BlockPos finalClosestAltar = closestAltar;
            double finalDstSqOtherRelay = dstSqOtherRelay;
            AstralSorcery.proxy.scheduleDelayed(() -> {
                TileAttunementRelay tar = MiscUtils.getTileAt(world, pos, TileAttunementRelay.class, true);
                if(tar != null) {
                    tar.updatePositionData(finalClosestAltar, finalDstSqOtherRelay);
                }
                if(recUpdate) {
                    BlockGodAltar.startSearchForRelayUpdate(world, pos);
                }
            });
        });
        searchThread.setName("[AstralAltar] AttRelay PositionFinder at " + pos.toString());
        searchThread.start();
    }
}
