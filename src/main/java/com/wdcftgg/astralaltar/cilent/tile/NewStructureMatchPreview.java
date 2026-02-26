package com.wdcftgg.astralaltar.cilent.tile;

import com.wdcftgg.astralaltar.init.multiblock.NewStructureRegistry;
import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Map;

public class NewStructureMatchPreview {

    private final TileEntity tile;
    private int timeout;

    public NewStructureMatchPreview(TileEntity tile) {
        this.tile = tile;
        this.timeout = 100;
    }

    public void tick() {
        PatternBlockArray pattern = NewStructureRegistry.getPatternBlockArray(tile);
        if (pattern != null && Minecraft.getMinecraft().player != null) {
            BlockPos at = tile.getPos();
            Vec3i v = pattern.getSize();
            int maxDim = Math.max(Math.max(v.getX(), v.getY()), v.getZ());
            maxDim = Math.max(9, maxDim);
            if (Minecraft.getMinecraft().player.getDistance(at.getX(), at.getY(), at.getZ()) <= maxDim) {
                resetTimeout();
                return;
            }
        }
        timeout--;
    }

    public void resetTimeout() {
        this.timeout = 300;
    }

    @Nullable
    public Integer getPreviewSlice() {
        PatternBlockArray pattern = NewStructureRegistry.getPatternBlockArray(tile);
        World world = Minecraft.getMinecraft().world;
        if (pattern == null || world == null) {
            return null;
        }
        int minY = pattern.getMin().getY();
        for (int y = minY; y <= pattern.getMax().getY(); y++) {
            if (!pattern.matchesSlice(world, tile.getPos(), y)) {
                return y;
            }
        }
        return null;
    }

    public boolean shouldBeRemoved() {
        return timeout <= 0 ||
                NewStructureRegistry.getPatternBlockArray(tile) == null ||
                Minecraft.getMinecraft().world == null ||
                Minecraft.getMinecraft().world.provider.getDimension() != ((TileEntity) tile).getWorld().provider.getDimension() ||
                NewStructureRegistry.getPatternBlockArray(tile).matches(Minecraft.getMinecraft().world, ((TileEntity) tile).getPos()) ||
                ((TileEntity) tile).isInvalid();
    }

    public boolean isOriginatingFrom(TileEntity tile) {
        if (!(tile instanceof TileEntity)) return false;
        if (shouldBeRemoved()) return false;
        return ((TileEntity) this.tile).getPos().equals(((TileEntity) tile).getPos());
    }

    public void renderPreview(float partialTicks) {
        PatternBlockArray pba = NewStructureRegistry.getPatternBlockArray(tile);
        World world = Minecraft.getMinecraft().world;
        Integer slice = getPreviewSlice();
        if(shouldBeRemoved() || pba == null || slice == null || world == null) {
            return;
        }

        BlockPos center = tile.getPos();

        IBlockAccess airWorld = new AirBlockRenderWorld(Biomes.PLAINS, WorldType.DEBUG_ALL_BLOCK_STATES);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        TextureHelper.setActiveTextureToAtlasSprite();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1F);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        GlStateManager.pushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);
        GlStateManager.translate(center.getX(), center.getY(), center.getZ());

        for (Map.Entry<BlockPos, BlockArray.BlockInformation> patternEntry : pba.getPatternSlice(slice).entrySet()) {
            BlockPos offset = patternEntry.getKey();
            BlockArray.BlockInformation info = patternEntry.getValue();

            if (offset.equals(BlockPos.ORIGIN) || pba.matchSingleBlock(world, center, offset)) {
                continue;
            }

            IBlockState state = world.getBlockState(center.add(offset));

            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            GlStateManager.pushMatrix();
            GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());
            GlStateManager.translate(0.125, 0.125, 0.125);
            GlStateManager.scale(0.75, 0.75, 0.75);

            if (state.getBlock().isAir(state, world, center.add(offset))) {
                RenderingUtils.renderBlockSafely(airWorld, BlockPos.ORIGIN, info.state, vb);
            } else {
                RenderingUtils.renderBlockSafelyWithOptionalColor(airWorld, BlockPos.ORIGIN, info.state, vb, 16711680);
            }

            tes.draw();
            GlStateManager.popMatrix();
        }

        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        TextureHelper.refreshTextureBindState();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

}
