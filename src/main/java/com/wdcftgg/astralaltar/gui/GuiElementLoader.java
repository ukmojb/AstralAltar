package com.wdcftgg.astralaltar.gui;

import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.blocks.tile.TileGodAltar;
import com.wdcftgg.astralaltar.gui.container.ContainerAltarGod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class GuiElementLoader implements IGuiHandler {

    public GuiElementLoader()
    {
        AstralAltar.Log("GuiElementLoader is Loading!!!");
        NetworkRegistry.INSTANCE.registerGuiHandler(AstralAltar.instance, this);
    }
    public static final int GUI_GodAltar = 0;


    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case GUI_GodAltar:
                TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
                if(te instanceof TileGodAltar)
                {
                    TileGodAltar godAltar = (TileGodAltar) te;
                    return new ContainerAltarGod(player.inventory, godAltar);
                }
                break;
            default:
                return null;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te;
        switch(ID)
        {
            case GUI_GodAltar:
                te = world.getTileEntity(new BlockPos(x, y, z));
                if(te instanceof TileGodAltar)
                {
                    TileGodAltar godAltar = (TileGodAltar) te;
                    return new GuiAltarGod(player.inventory, godAltar);
                }
                break;
            default:
                return null;
        }
        return null;
    }
}