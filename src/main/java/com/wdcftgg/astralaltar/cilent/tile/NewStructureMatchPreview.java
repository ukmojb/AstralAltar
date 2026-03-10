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

    @Nullable
    private final TileEntity tile;
    @Nullable
    private final PatternBlockArray directPattern;
    private final BlockPos origin;
    private int timeout;

    public NewStructureMatchPreview(TileEntity tile) {
        this(tile, NewStructureRegistry.getPatternBlockArray(tile), tile.getPos());
    }

    public NewStructureMatchPreview(@Nullable TileEntity tile, PatternBlockArray pattern, BlockPos origin) {
        this.tile = tile;
        this.directPattern = pattern;
        this.origin = origin;
        this.timeout = 100;
    }

    public void tick() {
        PatternBlockArray pattern = getPattern();
        if (pattern != null && Minecraft.getMinecraft().player != null) {
            BlockPos at = getOrigin();
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
        PatternBlockArray pattern = getPattern();
        World world = Minecraft.getMinecraft().world;
        if (pattern == null || world == null) {
            return null;
        }
        int minY = pattern.getMin().getY();
        BlockPos origin = getOrigin();
        for (int y = minY; y <= pattern.getMax().getY(); y++) {
            if (!pattern.matchesSlice(world, origin, y)) {
                return y;
            }
        }
        return null;
    }

    public boolean shouldBeRemoved() {
        PatternBlockArray pattern = getPattern();
        World world = Minecraft.getMinecraft().world;
        return timeout <= 0 ||
                pattern == null ||
                world == null ||
                tile != null && (tile.isInvalid() || tile.getWorld() == null || world.provider.getDimension() != tile.getWorld().provider.getDimension()) ||
                pattern.matches(world, getOrigin());
    }

    public boolean isOriginatingFrom(TileEntity tile) {
        if (shouldBeRemoved()) return false;
        return this.tile != null && this.tile.getPos().equals(tile.getPos());
    }

    public boolean isOriginatingFrom(@Nullable TileEntity tile, PatternBlockArray pattern, BlockPos origin) {
        if (shouldBeRemoved()) return false;
        return this.tile == tile && this.directPattern == pattern && this.origin.equals(origin);
    }

    public void renderPreview(float partialTicks) {
        PatternBlockArray pba = getPattern();
        World world = Minecraft.getMinecraft().world;
        Integer slice = getPreviewSlice();
        if(shouldBeRemoved() || pba == null || slice == null || world == null) {
            return;
        }

        BlockPos center = getOrigin();

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

    @Nullable
    private PatternBlockArray getPattern() {
        return directPattern != null ? directPattern : tile != null ? NewStructureRegistry.getPatternBlockArray(tile) : null;
    }

    private BlockPos getOrigin() {
        return origin;
    }
}
