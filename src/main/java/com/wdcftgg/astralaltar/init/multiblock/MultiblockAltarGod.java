package com.wdcftgg.astralaltar.init.multiblock;

import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.blocks.ModBlocks;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarbleSlab;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class MultiblockAltarGod extends PatternBlockArray {
    public MultiblockAltarGod() {
        super(new ResourceLocation(AstralAltar.MODID, "pattern_altar_t5"));
        this.addAll(MultiBlockArrays.patternAltarTrait);
        this.load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mrw = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mre = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState mcd = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mpl = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mbr = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);
        IBlockState mac = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState meg = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ENGRAVED);
        IBlockState msb = BlocksAS.blockMarbleSlab.getDefaultState().withProperty(BlockMarbleSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        IBlockState mst = BlocksAS.blockMarbleSlab.getDefaultState().withProperty(BlockMarbleSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
        IBlockState cc = BlocksAS.collectorCrystal.getDefaultState();
//        IBlockState ccc = BlocksAS.celestialCollectorCrystal.getDefaultState();

        //底部-大理石
        this.addBlock(5, -1, 1, mrw);
        this.addBlock(5, -1, 0, mrw);
        this.addBlock(6, -1, 0, mrw);
        this.addBlock(5, -1, -1, mrw);
        this.addBlock(-5, -1, 1, mrw);
        this.addBlock(-5, -1, 0, mrw);
        this.addBlock(-6, -1, 0, mrw);
        this.addBlock(-5, -1, -1, mrw);
        this.addBlock(-1, -1, 5, mrw);
        this.addBlock(0, -1, 5, mrw);
        this.addBlock(0, -1, 6, mrw);
        this.addBlock(1, -1, 5, mrw);
        this.addBlock(-1, -1, -5, mrw);
        this.addBlock(0, -1, -5, mrw);
        this.addBlock(0, -1, -6, mrw);
        this.addBlock(1, -1, -5, mrw);

        //底部-大理石砖
        this.addBlock(5, -1, 2, mbr);
        this.addBlock(5, -1, -2, mbr);
        this.addBlock(6, -1, 1, mbr);
        this.addBlock(6, -1, 2, mbr);
        this.addBlock(6, -1, -1, mbr);
        this.addBlock(6, -1, -2, mbr);
        this.addBlock(7, -1, 0, mbr);
        this.addBlock(7, -1, 1, mbr);
        this.addBlock(7, -1, -1, mbr);

        this.addBlock(-5, -1, 2, mbr);
        this.addBlock(-5, -1, -2, mbr);
        this.addBlock(-6, -1, 1, mbr);
        this.addBlock(-6, -1, 2, mbr);
        this.addBlock(-6, -1, -1, mbr);
        this.addBlock(-6, -1, -2, mbr);
        this.addBlock(-7, -1, 0, mbr);
        this.addBlock(-7, -1, 1, mbr);
        this.addBlock(-7, -1, -1, mbr);

        this.addBlock(2, -1, 5, mbr);
        this.addBlock(-2, -1, 5, mbr);
        this.addBlock(1, -1, 6, mbr);
        this.addBlock(2, -1, 6, mbr);
        this.addBlock(-1, -1, 6, mbr);
        this.addBlock(-2, -1, 6, mbr);
        this.addBlock(0, -1, 7, mbr);
        this.addBlock(1, -1, 7, mbr);
        this.addBlock(-1, -1, 7, mbr);

        this.addBlock(2, -1, -5, mbr);
        this.addBlock(-2, -1, -5, mbr);
        this.addBlock(1, -1, -6, mbr);
        this.addBlock(2, -1, -6, mbr);
        this.addBlock(-1, -1, -6, mbr);
        this.addBlock(-2, -1, -6, mbr);
        this.addBlock(0, -1, -7, mbr);
        this.addBlock(1, -1, -7, mbr);
        this.addBlock(-1, -1, -7, mbr);

        //底部-大理石半砖
        this.addBlock(8, -1, 0, msb);
        this.addBlock(8, -1, 1, msb);
        this.addBlock(8, -1, -1, msb);
        this.addBlock(-8, -1, 0, msb);
        this.addBlock(-8, -1, 1, msb);
        this.addBlock(-8, -1, -1, msb);
        this.addBlock(0, -1, 8, msb);
        this.addBlock(1, -1, 8, msb);
        this.addBlock(-1, -1, 8, msb);
        this.addBlock(0, -1, -8, msb);
        this.addBlock(1, -1, -8, msb);
        this.addBlock(-1, -1, -8, msb);

//        this.addBlock(7, -1, -2, msb);
//        this.addBlock(7, -1, 2, msb);
//        this.addBlock(-7, -1, -2, msb);
//        this.addBlock(-7, -1, 2, msb);
        this.addBlock(2, -1, 7, msb);
        this.addBlock(-2, -1, 7, msb);
        this.addBlock(2, -1, -7, msb);
        this.addBlock(-2, -1, -7, msb);
        this.addBlock(3, -1, 6, msb);
        this.addBlock(4, -1, 6, msb);
        this.addBlock(5, -1, 6, msb);
        this.addBlock(5, -1, 7, msb);
        this.addBlock(6, -1, 7, msb);
        this.addBlock(7, -1, 7, msb);
        this.addBlock(7, -1, 6, msb);
        this.addBlock(7, -1, 5, msb);
        this.addBlock(6, -1, 5, msb);
        this.addBlock(6, -1, 4, msb);
        this.addBlock(6, -1, 3, msb);
        this.addBlock(6, -1, -3, msb);
        this.addBlock(6, -1, -5, msb);
        this.addBlock(6, -1, -4, msb);
        this.addBlock(7, -1, -5, msb);
        this.addBlock(7, -1, -6, msb);
        this.addBlock(7, -1, -7, msb);
        this.addBlock(6, -1, -7, msb);
        this.addBlock(5, -1, -7, msb);
        this.addBlock(5, -1, -6, msb);
        this.addBlock(4, -1, -6, msb);
        this.addBlock(3, -1, -6, msb);
        this.addBlock(-3, -1, -6, msb);
        this.addBlock(-4, -1, -6, msb);
        this.addBlock(-5, -1, -6, msb);
        this.addBlock(-5, -1, -7, msb);
        this.addBlock(-6, -1, -7, msb);
        this.addBlock(-7, -1, -7, msb);
        this.addBlock(-7, -1, -6, msb);
        this.addBlock(-7, -1, -5, msb);
        this.addBlock(-6, -1, -5, msb);
        this.addBlock(-6, -1, -4, msb);
        this.addBlock(-6, -1, -3, msb);
        this.addBlock(-7, -1, -2, msb);
        this.addBlock(-7, -1, 2, msb);
        this.addBlock(-6, -1, 3, msb);
        this.addBlock(-6, -1, 4, msb);
        this.addBlock(-6, -1, 5, msb);
        this.addBlock(-7, -1, 5, msb);
        this.addBlock(-7, -1, 6, msb);
        this.addBlock(-7, -1, 7, msb);
        this.addBlock(-6, -1, 7, msb);
        this.addBlock(-5, -1, 7, msb);
        this.addBlock(-5, -1, 6, msb);
        this.addBlock(-4, -1, 6, msb);
        this.addBlock(-3, -1, 6, msb);

        //精致大理石柱子
        this.addBlock(6, -1, -6, mre);
        this.addBlock(-6, -1, -6, mre);
        this.addBlock(-6, -1, 6, mre);
        this.addBlock(6, -1, 6, mre);

        //大理石柱
        this.addBlock(6, 4, 6, mpl);
        this.addBlock(6, 3, 6, mpl);
        this.addBlock(6, 2, 6, mpl);
        this.addBlock(6, 1, 6, mpl);
        this.addBlock(6, 0, 6, mpl);
        this.addBlock(6, 0, -6, mpl);
        this.addBlock(6, 1, -6, mpl);
        this.addBlock(6, 2, -6, mpl);
        this.addBlock(6, 3, -6, mpl);
        this.addBlock(6, 4, -6, mpl);
        this.addBlock(-6, 0, -6, mpl);
        this.addBlock(-6, 1, -6, mpl);
        this.addBlock(-6, 2, -6, mpl);
        this.addBlock(-6, 3, -6, mpl);
        this.addBlock(-6, 4, -6, mpl);
        this.addBlock(-6, 0, 6, mpl);
        this.addBlock(-6, 1, 6, mpl);
        this.addBlock(-6, 2, 6, mpl);
        this.addBlock(-6, 3, 6, mpl);
        this.addBlock(-6, 4, 6, mpl);

        //顶部-大理石砖
        this.addBlock(-4, 6, -6, mbr);
        this.addBlock(-5, 6, -6, mbr);
        this.addBlock(-5, 5, -6, mbr);
        this.addBlock(-5, 6, -5, mbr);
        this.addBlock(-6, 5, -5, mbr);
        this.addBlock(-6, 6, -5, mbr);
        this.addBlock(-6, 6, -4, mbr);
        this.addBlock(-6, 6, 4, mbr);
        this.addBlock(-6, 6, 5, mbr);
        this.addBlock(-6, 5, 5, mbr);
        this.addBlock(-5, 6, 5, mbr);
        this.addBlock(-5, 5, 6, mbr);
        this.addBlock(-5, 6, 6, mbr);
        this.addBlock(-4, 6, 6, mbr);
        this.addBlock(4, 6, 6, mbr);
        this.addBlock(5, 6, 6, mbr);
        this.addBlock(5, 5, 6, mbr);
        this.addBlock(5, 6, 5, mbr);
        this.addBlock(6, 5, 5, mbr);
        this.addBlock(6, 6, 5, mbr);
        this.addBlock(6, 6, 4, mbr);
        this.addBlock(6, 6, -4, mbr);
        this.addBlock(6, 6, -5, mbr);
        this.addBlock(6, 5, -5, mbr);
        this.addBlock(5, 5, -6, mbr);
        this.addBlock(5, 6, -6, mbr);
        this.addBlock(4, 6, -6, mbr);
        this.addBlock(5, 6, -5, mbr);

        //顶部-雕纹大理石
        this.addBlock(-6, 5, -6, mcd);
        this.addBlock(-6, 5, 6, mcd);
        this.addBlock(6, 5, 6, mcd);
        this.addBlock(6, 5, -6, mcd);

        //顶部-大理石半砖-下
        this.addBlock(6, 6, -6, msb);
        this.addBlock(5, 7, -5, msb);
        this.addBlock(4, 7, -6, msb);
        this.addBlock(6, 7, -4, msb);
        this.addBlock(6, 6, 6, msb);
        this.addBlock(6, 7, 4, msb);
        this.addBlock(5, 7, 5, msb);
        this.addBlock(4, 7, 6, msb);
        this.addBlock(-4, 7, 6, msb);
        this.addBlock(-6, 6, 6, msb);
        this.addBlock(-5, 7, 5, msb);
        this.addBlock(-6, 7, 4, msb);
        this.addBlock(-6, 6, -6, msb);
        this.addBlock(-6, 7, -4, msb);
        this.addBlock(-5, 7, -5, msb);
        this.addBlock(-4, 7, -6, msb);

        //顶部-大理石半砖-上
        this.addBlock(4, 6, -5, mst);
        this.addBlock(5, 6, -4, mst);
        this.addBlock(5, 5, -5, mst);
        this.addBlock(5, 5, 5, mst);
        this.addBlock(5, 6, 4, mst);
        this.addBlock(4, 6, 5, mst);
        this.addBlock(-5, 5, 5, mst);
        this.addBlock(-4, 6, 5, mst);
        this.addBlock(-5, 6, 4, mst);
        this.addBlock(-5, 5, -5, mst);
        this.addBlock(-5, 6, -4, mst);
        this.addBlock(-4, 6, -5, mst);
        this.addBlock(-4, 7, 4, mst);
        this.addBlock(-4, 7, -4, mst);
        this.addBlock(4, 7, -4, mst);
        this.addBlock(4, 7, 4, mst);

        //顶部-大理石横梁
        this.addBlock(-1, 8, 4, mac);
        this.addBlock(-2, 8, 4, mac);
        this.addBlock(-3, 8, 4, mac);
        this.addBlock(-4, 8, 3, mac);
        this.addBlock(-4, 8, 2, mac);
        this.addBlock(-4, 8, 1, mac);
        this.addBlock(-4, 8, -1, mac);
        this.addBlock(-4, 8, -2, mac);
        this.addBlock(-4, 8, -3, mac);
        this.addBlock(-3, 8, -4, mac);
        this.addBlock(-2, 8, -4, mac);
        this.addBlock(-1, 8, -4, mac);
        this.addBlock(1, 8, -4, mac);
        this.addBlock(2, 8, -4, mac);
        this.addBlock(3, 8, -4, mac);
        this.addBlock(4, 8, -3, mac);
        this.addBlock(4, 8, -2, mac);
        this.addBlock(4, 8, -1, mac);
        this.addBlock(4, 8, 1, mac);
        this.addBlock(4, 8, 2, mac);
        this.addBlock(4, 8, 3, mac);
        this.addBlock(3, 8, 4, mac);
        this.addBlock(2, 8, 4, mac);
        this.addBlock(1, 8, 4, mac);

        //顶部-凹面大理石
        this.addBlock(5, 7, 3, meg);
        this.addBlock(5, 7, 4, meg);
        this.addBlock(4, 7, 5, meg);
        this.addBlock(4, 8, 4, meg);
        this.addBlock(3, 7, 5, meg);
        this.addBlock(-3, 7, 5, meg);
        this.addBlock(-4, 7, 5, meg);
        this.addBlock(-4, 8, 4, meg);
        this.addBlock(-5, 7, 4, meg);
        this.addBlock(-5, 7, 3, meg);
        this.addBlock(-5, 7, -3, meg);
        this.addBlock(-5, 7, -4, meg);
        this.addBlock(-4, 8, -4, meg);
        this.addBlock(-3, 7, -5, meg);
        this.addBlock(-4, 7, -5, meg);
        this.addBlock(3, 7, -5, meg);
        this.addBlock(4, 8, -4, meg);
        this.addBlock(5, 7, -4, meg);
        this.addBlock(4, 7, -5, meg);
        this.addBlock(5, 7, -3, meg);

        //聚能水晶
        this.addBlock(3, 6, 3, cc);
        this.addBlock(-3, 6, 3, cc);
        this.addBlock(-3, 6, -3, cc);
        this.addBlock(3, 6, -3, cc);

        //天体聚能水晶
//        this.addBlock(0, 8, 0, ccc);

        this.addBlock(0, 0, 0, ModBlocks.godAltar.getDefaultState());
    }
}
