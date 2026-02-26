package com.wdcftgg.astralaltar.proxy;

import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.cilent.ClientConnectionEventHandler;
import com.wdcftgg.astralaltar.cilent.effect.NewEffectHandler;
import com.wdcftgg.astralaltar.cilent.render.StarlightTextureStitcher;
import com.wdcftgg.astralaltar.cilent.tile.RenderAltarGod;
import com.wdcftgg.astralaltar.init.ParticleInit;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;

public class ClientProxy extends CommonProxy{

	public ClientProxy() {
	}


	public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

	public void onInit(){
		super.onInit();

		MinecraftForge.EVENT_BUS.register(NewEffectHandler.getInstance());
		MinecraftForge.EVENT_BUS.register(new ClientConnectionEventHandler());
	}



    public void onPreInit() {
        super.onPreInit();

//		RenderingRegistry.registerEntityRenderingHandler(EntityTimeCrack.class, RenderTimeCrack::new);

        registerTileRenderers();
        MinecraftForge.EVENT_BUS.register(new StarlightTextureStitcher());


//		AddedRegistryRecipes.initAstralRecipes();

//		ClientRegistry.bindTileEntitySpecialRenderer(HourGlassEntity.class, new HourGrassRender());
	}

	public void onPostInit() {
		super.onPostInit();

		ParticleInit.registerParticle();



//		AddedCraftingAccessManager.compile();
	}

	public static List<LayerRenderer<EntityLivingBase>> getLayerRenderers(RenderPlayer instance) {
		return (List)getPrivateValue(RenderLivingBase.class, instance, "layerRenderers");
	}

	private static <T> Object getPrivateValue(Class<T> clazz, T instance, String name) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(clazz, instance, name);
		} catch (Exception var4) {
			return null;
		}
	}

	private void registerTileRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileGodAltar.class, new RenderAltarGod());
	}

}
