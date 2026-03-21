package com.wdcftgg.astralaltar.blocks;

import com.google.common.collect.Lists;
import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.init.ModCreativeTab;
import com.wdcftgg.astralaltar.items.ModItems;
import com.wdcftgg.astralaltar.util.IHasModel;
import hellfirepvp.astralsorcery.common.block.BlockAttunementRelay;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.wdcftgg.astralaltar.gui.GuiElementLoader.GUI_GodAltar;

public class BlockGodAltar extends BlockAltar implements IHasModel {
    public static PropertyBool RENDER_FULLY = PropertyBool.create("render");
    private static final List<AxisAlignedBB> COLLISION_BOXES = Lists.newArrayList(
            // Core altar
            box(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
            box(0.25D, 0.25D, 0.25D, 0.75D, 0.5D, 0.75D),
            box(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D),
            // Floating outer pieces from bbmodel (including coordinates outside the base block)
            box(-0.3125D, 0.75D, 1.0625D, -0.0625D, 1.25D, 1.3125D),
            box(0.0D, 0.8125D, 1.0625D, 0.25D, 1.1875D, 1.3125D),
            box(0.375D, 0.625D, 1.0625D, 0.625D, 1.125D, 1.3125D),
            box(0.75D, 0.8125D, 1.0625D, 1.0D, 1.1875D, 1.3125D),
            box(1.0625D, 0.75D, 1.0625D, 1.3125D, 1.25D, 1.3125D),
            box(1.0625D, 0.8125D, 0.75D, 1.3125D, 1.1875D, 1.0D),
            box(1.0625D, 0.625D, 0.375D, 1.3125D, 1.125D, 0.625D),
            box(1.0625D, 0.8125D, 0.0D, 1.3125D, 1.1875D, 0.25D),
            box(1.0625D, 0.75D, -0.3125D, 1.3125D, 1.25D, -0.0625D),
            // bbmodel had y=12.5..18.5 here; round up to y=13..19 as requested.
            box(0.75D, 0.8125D, -0.3125D, 1.0D, 1.1875D, -0.0625D),
            box(0.375D, 0.625D, -0.3125D, 0.625D, 1.125D, -0.0625D),
            box(0.0D, 0.8125D, -0.3125D, 0.25D, 1.1875D, -0.0625D),
            box(-0.3125D, 0.75D, -0.3125D, -0.0625D, 1.25D, -0.0625D),
            box(-0.3125D, 0.8125D, 0.0D, -0.0625D, 1.1875D, 0.25D),
            box(-0.3125D, 0.625D, 0.375D, -0.0625D, 1.125D, 0.625D),
            box(-0.3125D, 0.8125D, 0.75D, -0.0625D, 1.1875D, 1.0D),
            box(-0.625D, 0.9375D, 0.125D, -0.375D, 1.3125D, 0.375D),
            box(0.125D, 0.9375D, -0.625D, 0.375D, 1.3125D, -0.375D),
            box(0.625D, 0.9375D, -0.625D, 0.875D, 1.3125D, -0.375D),
            box(-0.625D, 0.8125D, 0.375D, -0.375D, 1.3125D, 0.625D),
//            box(-0.625D, 1.0D, 1.375D, -0.375D, 1.5D, 1.625D),
//            box(-0.625D, 1.0D, -0.625D, -0.375D, 1.5D, -0.375D),
//            box(1.375D, 1.0D, -0.625D, 1.625D, 1.5D, -0.375D),
//            box(1.375D, 1.0D, 1.375D, 1.625D, 1.5D, 1.625D),
            box(-0.625D, 0.9375D, 0.625D, -0.375D, 1.3125D, 0.875D),
            box(1.375D, 0.9375D, 0.125D, 1.625D, 1.3125D, 0.375D),
            box(1.375D, 0.8125D, 0.375D, 1.625D, 1.3125D, 0.625D),
            box(0.375D, 0.8125D, -0.625D, 0.625D, 1.3125D, -0.375D),
            box(0.375D, 0.8125D, 1.375D, 0.625D, 1.3125D, 1.625D),
            box(0.125D, 0.9375D, 1.375D, 0.375D, 1.3125D, 1.625D),
            box(0.625D, 0.9375D, 1.375D, 0.875D, 1.3125D, 1.625D),
            box(1.375D, 0.9375D, 0.625D, 1.625D, 1.3125D, 0.875D)
    );

    public BlockGodAltar() {
        super();
        this.setHardness(3.0F);
        this.setSoundType(SoundType.STONE);
        this.setResistance(25.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setTranslationKey(AstralAltar.MODID + ".god_altar");
        this.setRegistryName("god_altar");
        this.setCreativeTab(ModCreativeTab.Tab);

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));

        this.setDefaultState(this.blockState.getBaseState());

    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileGodAltar te = (TileGodAltar) worldIn.getTileEntity(pos);
        if (te != null) {
            playerIn.openGui(AstralAltar.instance, GUI_GodAltar, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        startSearchForRelayUpdate(worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        startSearchForRelayUpdate(worldIn, pos);

        TileGodAltar te = (TileGodAltar) worldIn.getTileEntity(pos);
        if (te != null) {
            te.onBreak();
        }

        if (te != null && !worldIn.isRemote) {
            for (EnumFacing face : EnumFacing.VALUES) {
                IItemHandler handle = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
                if (handle != null) {
                    ItemUtils.dropInventory(handle, worldIn, pos);
                    break;
                }
            }
        }

        worldIn.removeTileEntity(pos);

    }

    public static void startSearchForRelayUpdate(World world, BlockPos pos) {
        Thread searchThread = new Thread(() -> {
            BlockArray relaysAndAltars = BlockDiscoverer.searchForBlocksAround(world, pos, 16, new BlockStateCheck.Block(BlocksAS.attunementRelay));
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : relaysAndAltars.getPattern().entrySet()) {
                BlockAttunementRelay.startSearchRelayLinkThreadAt(world, entry.getKey(), false);
            }
        });
        searchThread.setName("AttRelay UpdateFinder at " + pos.toString());
        searchThread.start();
    }

//    @Override
//    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
//        return Block.FULL_BLOCK_AABB;
//    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
    }

    private static List<AxisAlignedBB> getCollisionBoxList()
    {
        return COLLISION_BOXES;
    }

    private static AxisAlignedBB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ
        );
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGodAltar();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{ALTAR_TYPE, RENDER_FULLY});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int lvl = stack.getItemDamage();
        TileGodAltar ta = (TileGodAltar) MiscUtils.getTileAt(worldIn, pos, TileGodAltar.class, true);
        if (ta != null) {
            ta.onPlace(TileGodAltar.AltarLevel.values()[lvl]);
        }

    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
//        super.harvestBlock(worldIn, player, pos, state, te, stack);
//        if (!worldIn.isRemote && te != null && te instanceof TileAltar) {
//            ItemStack out = new ItemStack(BlocksAS.blockAltar, 1, this.damageDropped(state));
//            ItemUtils.dropItemNaturally(worldIn, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, out);
//        }

    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return super.getPickBlock(world.getBlockState(pos), target, world, pos, player);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getStateFromMeta(meta);
    }


    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void registerModels() {
        AstralAltar.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}