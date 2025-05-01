package com.wdcftgg.wdcftggTemplates;

import com.wdcftgg.wdcftggTemplates.init.RegistryHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = WdcftggLib.MODID, name = WdcftggLib.NAME, version = WdcftggLib.VERSION)
public class WdcftggLib {
    public static final String MODID = "wdcftgglib";
    public static final String NAME = "WdcftggLib";
    public static final String VERSION = "1.0.0";
    public static Logger logger;

    @Mod.Instance
    public static WdcftggLib instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        RegistryHandler.preInitRegistries(event);

    }


    @Mod.EventHandler
    public static void Init(FMLInitializationEvent event) {


    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RegistryHandler.postInitReg();
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
