package com.wdcftgg.wdcftggTemplates.init;


import com.wdcftgg.wdcftggTemplates.WdcftggLib;
import com.wdcftgg.wdcftggTemplates.blocks.ModBlocks;
import com.wdcftgg.wdcftggTemplates.items.ModItems;
import com.wdcftgg.wdcftggTemplates.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class RegistryHandler {
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{


		for (Item item : ModItems.ITEMS.toArray(new Item[0])){
			item.setTranslationKey(WdcftggLib.MODID + "." + item.getTranslationKey().replace("item.", ""));
		}
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		for (Block block : ModBlocks.BLOCKS.toArray(new Block[0])){
			block.setTranslationKey(WdcftggLib.MODID + "." + block.getTranslationKey().replace("tile.", ""));
		}
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



	public static void serverRegistries(FMLServerStartingEvent event)
    {
        //event.registerServerCommand(new CommandDimTeleport());
    }

	@SubscribeEvent
	public static void onRegisterSoundEvents(RegistryEvent.Register<SoundEvent> event)
	{
//		ResourceLocation location3 = new ResourceLocation(SpaceTime.MODID, "fallsword");
//
//		ModSounds.FALLSWORD = new SoundEvent(location3).setRegistryName(location3);
//
//		event.getRegistry().register(ModSounds.FALLSWORD);
	}

	private static Item newItemBlock(Block block){
		return new ItemBlock(block).setRegistryName(block.getRegistryName()).setTranslationKey(block.getTranslationKey());
	}
}
