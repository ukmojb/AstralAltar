package com.wdcftgg.astralaltar.cilent.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StarlightTextureStitcher {

    private static final ResourceLocation STARLIGHT_STILL = new ResourceLocation("astralsorcery:blocks/fluid/starlight_still");

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        if (map == Minecraft.getMinecraft().getTextureMapBlocks()) {
            map.registerSprite(STARLIGHT_STILL);
        }
    }
}
