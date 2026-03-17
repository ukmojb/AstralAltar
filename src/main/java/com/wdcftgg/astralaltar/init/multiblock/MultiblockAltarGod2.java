package com.wdcftgg.astralaltar.init.multiblock;

import com.google.common.base.Optional;
import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.blocks.ModBlocks;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

/*
【潜水】唐轩宇 1:23:59
ai的超绝维护性代码

【潜水】唐轩宇 1:24:11
我就问还有谁

【潜水】我错了，不和冶炼炉乱搞了，我们和好吧 1:24:59
@唐轩宇 这吧唧是啥

【潜水】唐轩宇 1:25:40
我让ai写了个读取nbt文件来自动生成星辉的结构文件java

【潜水】唐轩宇 1:25:50
然后就做了这个玩意

【管理员】 Circulation 0.0.1 Community 1:25:59
 唐轩宇
 我就问还有谁
是打表

【管理员】 Circulation 0.0.1 Community 1:26:01
我们没救了

【潜水】唐轩宇 1:27:01
算了，就堆在那吧
*/

public class MultiblockAltarGod2 extends PatternBlockArray {
    public MultiblockAltarGod2() {
        super(new ResourceLocation(AstralAltar.MODID, "pattern_altar_t5_2"));
        this.load();
    }

    private void load() {
        IBlockState mac = state("astralsorcery:blockmarble", "marbletype", "arch");
        IBlockState mbr = state("astralsorcery:blockmarble", "marbletype", "bricks");
        IBlockState mcd = state("astralsorcery:blockmarble", "marbletype", "chiseled");
        IBlockState meg = state("astralsorcery:blockmarble", "marbletype", "engraved");
        IBlockState mpl = state("astralsorcery:blockmarble", "marbletype", "pillar");
        IBlockState mrw = state("astralsorcery:blockmarble", "marbletype", "raw");
        IBlockState mre = state("astralsorcery:blockmarble", "marbletype", "runed");
        IBlockState bma = state("astralsorcery:blockblackmarble", "marbletype", "arch");
        IBlockState bme = state("astralsorcery:blockblackmarble", "marbletype", "engraved");
        IBlockState bmr = state("astralsorcery:blockblackmarble", "marbletype", "raw");
        IBlockState sea = state("minecraft:sea_lantern");
        IBlockState mdb = state("astralsorcery:blockmarbledoubleslab", "marbletype", "bricks");
        IBlockState relay = state("astralsorcery:blockattunementrelay");
        IBlockState cc = state("astralsorcery:blockcollectorcrystal");
        IBlockState msb = state("astralsorcery:blockmarbleslab", "half", "bottom", "marbletype", "bricks");
        IBlockState mst = state("astralsorcery:blockmarbleslab", "half", "top", "marbletype", "bricks");

        // mac
        addAll(mac,
                -8, -1, -4, -8, -1, 4, -7, -1, -5, -7, -1, -3, -7, -1, 3, -7, -1, 5, -6, -1, -4, -6, -1, 4, -5, -1, -7, -5, -1, 7,
                -4, -1, -8, -4, -1, -6, -4, -1, 6, -4, -1, 8, -3, -1, -7, -3, -1, 7, 3, -1, -7, 3, -1, 7, 4, -1, -8, 4, -1, -6,
                4, -1, 6, 4, -1, 8, 5, -1, -7, 5, -1, 7, 6, -1, -4, 6, -1, 4, 7, -1, -5, 7, -1, -3, 7, -1, 3, 7, -1, 5,
                8, -1, -4, 8, -1, 4, -8, 5, -2, -8, 5, -1, -8, 5, 0, -8, 5, 1, -8, 5, 2, -2, 5, -8, -2, 5, 8, -1, 5, -8,
                -1, 5, 8, 0, 5, -8, 0, 5, 8, 1, 5, -8, 1, 5, 8, 2, 5, -8, 2, 5, 8, 8, 5, -2, 8, 5, -1, 8, 5, 0,
                8, 5, 1, 8, 5, 2, -4, 8, -3, -4, 8, -2, -4, 8, 2, -4, 8, 3, -3, 8, -4, -3, 8, 4, -2, 8, -4, -2, 8, 4,
                2, 8, -4, 2, 8, 4, 3, 8, -4, 3, 8, 4, 4, 8, -3, 4, 8, -2, 4, 8, 2, 4, 8, 3, -3, 9, -1, -3, 9, 0,
                -3, 9, 1, -2, 9, -2, -2, 9, 2, -1, 9, -3, -1, 9, 3, 0, 9, -3, 0, 9, 3, 1, 9, -3, 1, 9, 3, 2, 9, -2,
                2, 9, 2, 3, 9, -1, 3, 9, 0, 3, 9, 1);

        // mbr
        addAll(mbr,
                -10, -1, -4, -10, -1, -3, -10, -1, -2, -10, -1, -1, -10, -1, 0, -10, -1, 1, -10, -1, 2, -10, -1, 3, -10, -1, 4, -9, -1, -5,
                -9, -1, -4, -9, -1, -3, -9, -1, -2, -9, -1, -1, -9, -1, 0, -9, -1, 1, -9, -1, 2, -9, -1, 3, -9, -1, 4, -9, -1, 5,
                -8, -1, -6, -8, -1, -2, -8, -1, 2, -8, -1, 6, -7, -1, -7, -7, -1, -6, -7, -1, -2, -7, -1, -1, -7, -1, 1, -7, -1, 2,
                -7, -1, 6, -7, -1, 7, -6, -1, -8, -6, -1, -7, -6, -1, -2, -6, -1, -1, -6, -1, 1, -6, -1, 2, -6, -1, 7, -6, -1, 8,
                -5, -1, -9, -5, -1, -5, -5, -1, -4, -5, -1, -3, -5, -1, -2, -5, -1, 2, -5, -1, 3, -5, -1, 4, -5, -1, 5, -5, -1, 9,
                -4, -1, -10, -4, -1, -9, -4, -1, -5, -4, -1, -2, -4, -1, -1, -4, -1, 0, -4, -1, 1, -4, -1, 2, -4, -1, 5, -4, -1, 9,
                -4, -1, 10, -3, -1, -10, -3, -1, -9, -3, -1, -5, -3, -1, 5, -3, -1, 9, -3, -1, 10, -2, -1, -10, -2, -1, -9, -2, -1, -8,
                -2, -1, -7, -2, -1, -6, -2, -1, -5, -2, -1, -4, -2, -1, 4, -2, -1, 5, -2, -1, 6, -2, -1, 7, -2, -1, 8, -2, -1, 9,
                -2, -1, 10, -1, -1, -10, -1, -1, -9, -1, -1, -7, -1, -1, -6, -1, -1, -4, -1, -1, 4, -1, -1, 6, -1, -1, 7, -1, -1, 9,
                -1, -1, 10, 0, -1, -10, 0, -1, -9, 0, -1, -4, 0, -1, 4, 0, -1, 9, 0, -1, 10, 1, -1, -10, 1, -1, -9, 1, -1, -7,
                1, -1, -6, 1, -1, -4, 1, -1, 4, 1, -1, 6, 1, -1, 7, 1, -1, 9, 1, -1, 10, 2, -1, -10, 2, -1, -9, 2, -1, -8,
                2, -1, -7, 2, -1, -6, 2, -1, -5, 2, -1, -4, 2, -1, 4, 2, -1, 5, 2, -1, 6, 2, -1, 7, 2, -1, 8, 2, -1, 9,
                2, -1, 10, 3, -1, -10, 3, -1, -9, 3, -1, -5, 3, -1, 5, 3, -1, 9, 3, -1, 10, 4, -1, -10, 4, -1, -9, 4, -1, -5,
                4, -1, -2, 4, -1, -1, 4, -1, 0, 4, -1, 1, 4, -1, 2, 4, -1, 5, 4, -1, 9, 4, -1, 10, 5, -1, -9, 5, -1, -5,
                5, -1, -4, 5, -1, -3, 5, -1, -2, 5, -1, 2, 5, -1, 3, 5, -1, 4, 5, -1, 5, 5, -1, 9, 6, -1, -8, 6, -1, -7,
                6, -1, -2, 6, -1, -1, 6, -1, 1, 6, -1, 2, 6, -1, 7, 6, -1, 8, 7, -1, -7, 7, -1, -6, 7, -1, -2, 7, -1, -1,
                7, -1, 1, 7, -1, 2, 7, -1, 6, 7, -1, 7, 8, -1, -6, 8, -1, -2, 8, -1, 2, 8, -1, 6, 9, -1, -5, 9, -1, -4,
                9, -1, -3, 9, -1, -2, 9, -1, -1, 9, -1, 0, 9, -1, 1, 9, -1, 2, 9, -1, 3, 9, -1, 4, 9, -1, 5, 10, -1, -4,
                10, -1, -3, 10, -1, -2, 10, -1, -1, 10, -1, 0, 10, -1, 1, 10, -1, 2, 10, -1, 3, 10, -1, 4, -4, 3, -3, -4, 3, 3,
                -3, 3, -4, -3, 3, 4, 3, 3, -4, 3, 3, 4, 4, 3, -3, 4, 3, 3, -3, 4, -3, -3, 4, -2, -3, 4, -1, -3, 4, 1,
                -3, 4, 2, -3, 4, 3, -2, 4, -3, -2, 4, 3, -1, 4, -3, -1, 4, 3, 1, 4, -3, 1, 4, 3, 2, 4, -3, 2, 4, 3,
                3, 4, -3, 3, 4, -2, 3, 4, -1, 3, 4, 1, 3, 4, 2, 3, 4, 3, -6, 5, -5, -6, 5, 5, -5, 5, -6, -5, 5, 6,
                5, 5, -6, 5, 5, 6, 6, 5, -5, 6, 5, 5, -6, 6, -5, -6, 6, -4, -6, 6, 4, -6, 6, 5, -5, 6, -6, -5, 6, -5,
                -5, 6, 5, -5, 6, 6, -4, 6, -6, -4, 6, 6, 4, 6, -6, 4, 6, 6, 5, 6, -6, 5, 6, -5, 5, 6, 5, 5, 6, 6,
                6, 6, -5, 6, 6, -4, 6, 6, 4, 6, 6, 5);

        // mcd
        addAll(mcd,
                -8, -1, -5, -8, -1, -3, -8, -1, 3, -8, -1, 5, -6, -1, -5, -6, -1, -3, -6, -1, 3, -6, -1, 5, -5, -1, -8, -5, -1, -6,
                -5, -1, 6, -5, -1, 8, -3, -1, -8, -3, -1, -6, -3, -1, 6, -3, -1, 8, 3, -1, -8, 3, -1, -6, 3, -1, 6, 3, -1, 8,
                5, -1, -8, 5, -1, -6, 5, -1, 6, 5, -1, 8, 6, -1, -5, 6, -1, -3, 6, -1, 3, 6, -1, 5, 8, -1, -5, 8, -1, -3,
                8, -1, 3, 8, -1, 5, -4, 3, -4, -4, 3, 4, 4, 3, -4, 4, 3, 4, -6, 5, -6, -6, 5, 6, 6, 5, -6, 6, 5, 6);

        // meg
        addAll(meg,
                -5, -1, 0, 0, -1, -5, 0, -1, 5, 5, -1, 0, -5, 7, -4, -5, 7, -3, -5, 7, 3, -5, 7, 4, -4, 7, -5, -4, 7, 5,
                -3, 7, -5, -3, 7, 5, 3, 7, -5, 3, 7, 5, 4, 7, -5, 4, 7, 5, 5, 7, -4, 5, 7, -3, 5, 7, 3, 5, 7, 4,
                -4, 8, -4, -4, 8, 4, 4, 8, -4, 4, 8, 4);

        // mpl
        addAll(mpl,
                -8, 0, -3, -8, 0, 3, -6, 0, -6, -6, 0, 6, -3, 0, -8, -3, 0, 8, 3, 0, -8, 3, 0, 8, 6, 0, -6, 6, 0, 6,
                8, 0, -3, 8, 0, 3, -8, 1, -3, -8, 1, 3, -6, 1, -6, -6, 1, 6, -4, 1, -4, -4, 1, 4, -3, 1, -8, -3, 1, 8,
                3, 1, -8, 3, 1, 8, 4, 1, -4, 4, 1, 4, 6, 1, -6, 6, 1, 6, 8, 1, -3, 8, 1, 3, -8, 2, -3, -8, 2, 3,
                -6, 2, -6, -6, 2, 6, -4, 2, -4, -4, 2, 4, -3, 2, -8, -3, 2, 8, 3, 2, -8, 3, 2, 8, 4, 2, -4, 4, 2, 4,
                6, 2, -6, 6, 2, 6, 8, 2, -3, 8, 2, 3, -8, 3, -3, -8, 3, 3, -6, 3, -6, -6, 3, 6, -3, 3, -8, -3, 3, 8,
                3, 3, -8, 3, 3, 8, 6, 3, -6, 6, 3, 6, 8, 3, -3, 8, 3, 3, -8, 4, -3, -8, 4, 3, -6, 4, -6, -6, 4, 6,
                -3, 4, -8, -3, 4, 8, 3, 4, -8, 3, 4, 8, 6, 4, -6, 6, 4, 6, 8, 4, -3, 8, 4, 3);

        // mrw
        addAll(mrw,
                -4, -1, -4, -4, -1, -3, -4, -1, 3, -4, -1, 4, -3, -1, -4, -3, -1, 4, 3, -1, -4, 3, -1, 4, 4, -1, -4, 4, -1, -3,
                4, -1, 3, 4, -1, 4);

        // mre
        addAll(mre,
                -6, -1, -6, -6, -1, 0, -6, -1, 6, -5, -1, -1, -5, -1, 1, -1, -1, -5, -1, -1, 5, 0, -1, -6, 0, -1, 6, 1, -1, -5,
                1, -1, 5, 5, -1, -1, 5, -1, 1, 6, -1, -6, 6, -1, 0, 6, -1, 6, -4, 0, -4, -4, 0, 4, 4, 0, -4, 4, 0, 4);

        // bma
        addAll(bma,
                -8, -1, -1, -8, -1, 1, -7, -1, 0, -1, -1, -8, -1, -1, 8, 0, -1, -7, 0, -1, 7, 1, -1, -8, 1, -1, 8, 7, -1, 0,
                8, -1, -1, 8, -1, 1);

        // bme
        addAll(bme,
                -8, -1, 0, 0, -1, -8, 0, -1, 8, 8, -1, 0);

        // bmr
        addAll(bmr,
                -7, -1, -4, -7, -1, 4, -4, -1, -7, -4, -1, 7, -3, -1, -3, -3, -1, -2, -3, -1, -1, -3, -1, 0, -3, -1, 1, -3, -1, 2,
                -3, -1, 3, -2, -1, -3, -2, -1, -2, -2, -1, -1, -2, -1, 0, -2, -1, 1, -2, -1, 2, -2, -1, 3, -1, -1, -3, -1, -1, -2,
                -1, -1, -1, -1, -1, 0, -1, -1, 1, -1, -1, 2, -1, -1, 3, 0, -1, -3, 0, -1, -2, 0, -1, -1, 0, -1, 0, 0, -1, 1,
                0, -1, 2, 0, -1, 3, 1, -1, -3, 1, -1, -2, 1, -1, -1, 1, -1, 0, 1, -1, 1, 1, -1, 2, 1, -1, 3, 2, -1, -3,
                2, -1, -2, 2, -1, -1, 2, -1, 0, 2, -1, 1, 2, -1, 2, 2, -1, 3, 3, -1, -3, 3, -1, -2, 3, -1, -1, 3, -1, 0,
                3, -1, 1, 3, -1, 2, 3, -1, 3, 4, -1, -7, 4, -1, 7, 7, -1, -4, 7, -1, 4);

        // sea
        addAll(sea,
                -8, 5, -3, -8, 5, 3, -3, 5, -8, -3, 5, 8, 3, 5, -8, 3, 5, 8, 8, 5, -3, 8, 5, 3);

        // mdb
        addAll(mdb,
                -7, 5, -4, -7, 5, 4, -4, 5, -7, -4, 5, 7, 4, 5, -7, 4, 5, 7, 7, 5, -4, 7, 5, 4);

        // relay
        addAll(relay,
                -7, 0, -4, -7, 0, 4, -4, 0, -7, -4, 0, 7, 4, 0, -7, 4, 0, 7, 7, 0, -4, 7, 0, 4);

        // cc
        addAll(cc, MultiblockAltarGod2::isCollectorCrystalLike,
                -3, 6, -3, -3, 6, 3, 3, 6, -3, 3, 6, 3);

        // msb
        addAll(msb,
                -11, -1, -4, -11, -1, -3, -11, -1, -2, -11, -1, -1, -11, -1, 0, -11, -1, 1, -11, -1, 2, -11, -1, 3, -11, -1, 4, -10, -1, -5,
                -10, -1, 5, -9, -1, -6, -9, -1, 6, -8, -1, -7, -8, -1, 7, -7, -1, -8, -7, -1, 8, -6, -1, -9, -6, -1, 9, -5, -1, -10,
                -5, -1, 10, -4, -1, -11, -4, -1, 11, -3, -1, -11, -3, -1, 11, -2, -1, -11, -2, -1, 11, -1, -1, -11, -1, -1, 11, 0, -1, -11,
                0, -1, 11, 1, -1, -11, 1, -1, 11, 2, -1, -11, 2, -1, 11, 3, -1, -11, 3, -1, 11, 4, -1, -11, 4, -1, 11, 5, -1, -10,
                5, -1, 10, 6, -1, -9, 6, -1, 9, 7, -1, -8, 7, -1, 8, 8, -1, -7, 8, -1, 7, 9, -1, -6, 9, -1, 6, 10, -1, -5,
                10, -1, 5, 11, -1, -4, 11, -1, -3, 11, -1, -2, 11, -1, -1, 11, -1, 0, 11, -1, 1, 11, -1, 2, 11, -1, 3, 11, -1, 4,
                -4, 4, -3, -4, 4, 3, -3, 4, -4, -3, 4, 4, 3, 4, -4, 3, 4, 4, 4, 4, -3, 4, 4, 3, -3, 5, -3, -3, 5, 3,
                3, 5, -3, 3, 5, 3, -7, 6, -3, -7, 6, -2, -7, 6, -1, -7, 6, 0, -7, 6, 1, -7, 6, 2, -7, 6, 3, -6, 6, -6,
                -6, 6, 6, -3, 6, -7, -3, 6, 7, -2, 6, -7, -2, 6, 7, -1, 6, -7, -1, 6, 7, 0, 6, -7, 0, 6, 7, 1, 6, -7,
                1, 6, 7, 2, 6, -7, 2, 6, 7, 3, 6, -7, 3, 6, 7, 6, 6, -6, 6, 6, 6, 7, 6, -3, 7, 6, -2, 7, 6, -1,
                7, 6, 0, 7, 6, 1, 7, 6, 2, 7, 6, 3, -6, 7, -4, -6, 7, 4, -5, 7, -5, -5, 7, 5, -4, 7, -6, -4, 7, 6,
                4, 7, -6, 4, 7, 6, 5, 7, -5, 5, 7, 5, 6, 7, -4, 6, 7, 4, -4, 8, -1, -4, 8, 0, -4, 8, 1, -1, 8, -4,
                -1, 8, 4, 0, 8, -4, 0, 8, 4, 1, 8, -4, 1, 8, 4, 4, 8, -1, 4, 8, 0, 4, 8, 1, -3, 9, -2, -3, 9, 2,
                -2, 9, -3, -2, 9, 3, 2, 9, -3, 2, 9, 3, 3, 9, -2, 3, 9, 2);

        // mst
        addAll(mst,
                -3, 3, -3, -3, 3, -2, -3, 3, 2, -3, 3, 3, -2, 3, -3, -2, 3, 3, 2, 3, -3, 2, 3, 3, 3, 3, -3, 3, 3, -2,
                3, 3, 2, 3, 3, 3, -5, 5, -5, -5, 5, 5, 5, 5, -5, 5, 5, 5, -6, 6, -3, -6, 6, 0, -6, 6, 3, -5, 6, -4,
                -5, 6, 4, -4, 6, -5, -4, 6, 5, -3, 6, -6, -3, 6, 6, 0, 6, -6, 0, 6, 6, 3, 6, -6, 3, 6, 6, 4, 6, -5,
                4, 6, 5, 5, 6, -4, 5, 6, 4, 6, 6, -3, 6, 6, 0, 6, 6, 3, -4, 7, -4, -4, 7, 4, 4, 7, -4, 4, 7, 4);

        this.addBlock(0, 0, 0, ModBlocks.godAltar.getDefaultState());
    }

    private void addAll(IBlockState state, int... coords) {
        if (coords.length % 3 != 0) {
            throw new IllegalArgumentException("coords length must be a multiple of 3");
        }
        for (int i = 0; i < coords.length; i += 3) {
            this.addBlock(coords[i], coords[i + 1], coords[i + 2], state);
        }
    }

    private void addAll(IBlockState state, BlockStateCheck check, int... coords) {
        if (coords.length % 3 != 0) {
            throw new IllegalArgumentException("coords length must be a multiple of 3");
        }
        for (int i = 0; i < coords.length; i += 3) {
            this.addBlock(coords[i], coords[i + 1], coords[i + 2], state, check);
        }
    }

    private static boolean isCollectorCrystalLike(IBlockState state) {
        Block block = state.getBlock();
        return block == BlocksAS.collectorCrystal || block == BlocksAS.celestialCollectorCrystal;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private IBlockState state(String blockId, String... properties) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(blockId));
        if (block == null) {
            throw new IllegalArgumentException("Unknown block: " + blockId);
        }

        IBlockState state = block.getDefaultState();
        for (int i = 0; i < properties.length; i += 2) {
            String propertyName = properties[i];
            String propertyValue = properties[i + 1];
            IProperty property = block.getBlockState().getProperty(propertyName);
            if (property == null) {
                throw new IllegalArgumentException("Unknown property '" + propertyName + "' for block " + blockId);
            }

            Optional parsedValue = property.parseValue(propertyValue);
            if (!parsedValue.isPresent()) {
                throw new IllegalArgumentException("Invalid value '" + propertyValue + "' for property '" + propertyName + "' on block " + blockId);
            }
            state = state.withProperty(property, (Comparable) parsedValue.get());
        }
        return state;
    }
}
