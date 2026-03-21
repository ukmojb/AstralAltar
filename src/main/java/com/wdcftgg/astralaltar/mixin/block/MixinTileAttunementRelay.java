package com.wdcftgg.astralaltar.mixin.block;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileAttunementRelay.class, remap = false)
public abstract class MixinTileAttunementRelay extends TileEntity {

    @Shadow private BlockPos linked;
    @Shadow private float collectionMultiplier;
    @Shadow private boolean hasMultiblock;

    @Shadow public abstract boolean doesSeeSky();

    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/astralsorcery/common/util/MiscUtils;getTileAt(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Ljava/lang/Class;Z)Ljava/lang/Object;"
            ),
            cancellable = true
    )
    private void astralaltar$forwardRelayStarlightToGodAltar(CallbackInfo ci) {
        TileGodAltar godAltar = MiscUtils.getTileAt(this.getWorld(), this.linked, TileGodAltar.class, true);
        if (godAltar == null) {
            return;
        }

        if (this.hasMultiblock && this.doesSeeSky()) {
            WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(this.getWorld());
            int yLevel = this.getPos().getY();
            if (handle != null && yLevel > 40) {
                double coll = 0.3;
                float dstr;
                if (yLevel > 120) {
                    dstr = 1.0F;
                } else {
                    dstr = (float) (yLevel - 40) / 80.0F;
                }

                coll *= dstr;
                coll *= this.collectionMultiplier;
                coll *= 0.2 + 0.8 * ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(this.getWorld());
                godAltar.receiveStarlight(null, coll * 2);
            }
        }

        ci.cancel();
    }
}
