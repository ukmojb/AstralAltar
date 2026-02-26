package com.wdcftgg.astralaltar.cilent.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.util.Arrays;

public class BakedQuadRetextured extends BakedQuad {

    private final TextureAtlasSprite sprite;

    public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite sprite) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length),
                quad.getTintIndex(), quad.getFace(), sprite,
                quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.sprite = sprite;
        remapQuad(quad);
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return sprite;
    }

    private void remapQuad(BakedQuad quad) {
        TextureAtlasSprite from = quad.getSprite();
        if (from == null) {
            return;
        }

        VertexFormat format = getFormat();
        int[] data = getVertexData();
        int vertexSize = format.getIntegerSize();
        int uvOffset = findUvOffset(format);
        if (uvOffset < 0) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            int base = i * vertexSize + uvOffset;
            float u = Float.intBitsToFloat(data[base]);
            float v = Float.intBitsToFloat(data[base + 1]);
            float unU = from.getUnInterpolatedU(u);
            float unV = from.getUnInterpolatedV(v);
            data[base] = Float.floatToRawIntBits(sprite.getInterpolatedU(unU));
            data[base + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV(unV));
        }
    }

    private int findUvOffset(VertexFormat format) {
        for (int i = 0; i < format.getElementCount(); i++) {
            VertexFormatElement e = format.getElement(i);
            if (e.getUsage() == VertexFormatElement.EnumUsage.UV && e.getIndex() == 0) {
                return format.getOffset(i) / 4;
            }
        }
        return -1;
    }
}
