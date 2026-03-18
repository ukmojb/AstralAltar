package com.wdcftgg.astralaltar.init;


import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.blocks.ModBlocks;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.items.ModItems;
import com.wdcftgg.astralaltar.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.wdcftgg.astralaltar.AstralAltar.MODID;

@EventBusSubscriber
public class RegistryHandler {
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ModItems.ITEMS)
		{
			if (item instanceof IHasModel)
			{
				((IHasModel)item).registerModels();
			}
		}
		
		for(Block block : ModBlocks.BLOCKS)
		{
			if (block instanceof IHasModel)
			{
				((IHasModel)block).registerModels();
			}
		}

	}

	public static void preInitRegistries(FMLPreInitializationEvent event)
	{

//		ModEntityInit.registerEntities();


	}

	public static void postInitReg()
	{
		//WorldType TYPE_ONE = new WorldTypeOne();
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		AddedRegistryRecipes.initAstralRecipes();
	}

	public static void serverRegistries(FMLServerStartingEvent event)
    {
        //event.registerServerCommand(new CommandDimTeleport());
    }

	@SubscribeEvent
	public static void onRegisterSoundEvents(RegistryEvent.Register<SoundEvent> event)
	{
        ModSounds.oneLineC = registerSound(event, "one-lineC");
        ModSounds.oneLineD = registerSound(event, "one-lineD");
        ModSounds.oneLineE = registerSound(event, "one-lineE");
        ModSounds.oneLineF = registerSound(event, "one-lineF");
	}

	public static void registerTileEntity() {

		GameRegistry.registerTileEntity(TileGodAltar.class, new ResourceLocation(MODID, "GodAltarEntity"));
	}

	private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String fileName) {
		ResourceLocation location = new ResourceLocation(AstralAltar.MODID, fileName);

		SoundEvent soundEvent = new SoundEvent(location).setRegistryName(location);

		event.getRegistry().register(soundEvent);

        return soundEvent;
	}
}
