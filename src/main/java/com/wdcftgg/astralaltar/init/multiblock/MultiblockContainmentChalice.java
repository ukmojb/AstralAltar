package com.wdcftgg.astralaltar.init.multiblock;

import com.wdcftgg.astralaltar.AstralAltar;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockInfusedWood;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarbleSlab;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.block.BlockStairs.*;

public class MultiblockContainmentChalice extends PatternBlockArray {
    public MultiblockContainmentChalice() {
        super(new ResourceLocation(AstralAltar.MODID, "pattern_chalice"));
        this.load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState engraved = BlocksAS.blockBlackMarble.getDefaultState().withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.ENGRAVED);
        IBlockState arch = marble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState chiseled = BlocksAS.blockBlackMarble.getDefaultState().withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.CHISELED);
        IBlockState infusedWood = BlocksAS.blockInfusedWood.getDefaultState().withProperty(BlockInfusedWood.WOOD_TYPE, BlockInfusedWood.WoodType.ENRICHED);
        IBlockState stairn = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT).withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
        IBlockState stairw = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT).withProperty(FACING, EnumFacing.WEST).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
        IBlockState staire = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT).withProperty(FACING, EnumFacing.EAST).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
        IBlockState stairs = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT).withProperty(FACING, EnumFacing.SOUTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
        IBlockState slabb = BlocksAS.blockMarbleSlab.getDefaultState().withProperty(BlockMarbleSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);

        this.addBlock(0, -1, 0, infusedWood);

        this.addBlock(0, -2, 0, engraved);

        this.addBlock(0, -2, 1, arch);
        this.addBlock(0, -2, -1, arch);
        this.addBlock(1, -2, 0, arch);
        this.addBlock(-1, -2, 0, arch);

        this.addBlock(0, -2, 2, chiseled);
        this.addBlock(0, -2, -2, chiseled);
        this.addBlock(2, -2, 0, chiseled);
        this.addBlock(-2, -2, 0, chiseled);

        this.addBlock(1, -2, 2, slabb);
        this.addBlock(-1, -2, 2, slabb);
        this.addBlock(1, -2, -2, slabb);
        this.addBlock(-1, -2, -2, slabb);
        this.addBlock(2, -2, 1, slabb);
        this.addBlock(2, -2, -1, slabb);
        this.addBlock(-2, -2, 1, slabb);
        this.addBlock(-2, -2, -1, slabb);

        this.addBlock(0, -1, 1, stairn);
        this.addBlock(0, -1, -1, stairs);
        this.addBlock(-1, -1, 0, staire);
        this.addBlock(1, -1, 0, stairw);

        this.addBlock(0, 0, 0, BlocksAS.blockChalice.getDefaultState());
    }
}
