package com.wdcftgg.astralaltar.blocks;

import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

    public static List<Block> BLOCKS = new ArrayList<Block>();

    public static BlockGodAltar godAltar = new BlockGodAltar();
    public static Block starMetalBlock = new BlockBase("starmetal_block").setHardness(3.0F).setResistance(25.0F);
}
