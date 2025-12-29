package com.wdcftgg.astralaltar;

import com.wdcftgg.astralaltar.init.RegistryHandler;
import com.wdcftgg.astralaltar.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = AstralAltar.MODID, name = AstralAltar.NAME, version = AstralAltar.VERSION, dependencies="required-after:astralsorcery")
public class AstralAltar {
    public static final String MODID = "astralaltar";
    public static final String NAME = "AstralAltar";
    public static final String VERSION = "1.0.0";
    public static Logger logger;

    @Mod.Instance
    public static AstralAltar instance;

    public static final String CLIENT_PROXY_CLASS = "com.wdcftgg.astralaltar.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.wdcftgg.astralaltar.proxy.CommonProxy";

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        RegistryHandler.preInitRegistries(event);

        proxy.onPreInit();

    }


    @Mod.EventHandler
    public static void Init(FMLInitializationEvent event) {
        RegistryHandler.registerTileEntity();

        proxy.onInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RegistryHandler.postInitReg();
        proxy.onPostInit();
    }


    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        RegistryHandler.serverRegistries(event);
    }

    public static void LogWarning(String str, Object... args) {
        logger.warn(String.format(str, args));
    }

    public static void LogWarning(String str) {
        logger.warn(str);
    }

    public static void Log(String str) {
        logger.info(str);
    }

    public static void Log(String str, Object... args) {
        logger.info(String.format(str, args));
    }
}
