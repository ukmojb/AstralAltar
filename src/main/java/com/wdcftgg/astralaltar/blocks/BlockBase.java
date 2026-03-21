package com.wdcftgg.astralaltar.blocks;

import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.init.ModCreativeTab;
import com.wdcftgg.astralaltar.items.ModItems;
import com.wdcftgg.astralaltar.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel {

    public BlockBase(String name) {
        super(Material.IRON);
        this.setHarvestLevel("pickaxe", 2);
        this.setTranslationKey(AstralAltar.MODID + "." + name);
        this.setRegistryName(name);
        this.setCreativeTab(ModCreativeTab.Tab);

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }


    @Override
    public void registerModels() {
        AstralAltar.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

}
