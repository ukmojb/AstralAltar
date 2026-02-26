package com.wdcftgg.astralaltar.cilent;

import com.wdcftgg.astralaltar.AstralAltar;
import com.wdcftgg.astralaltar.cilent.effect.NewEffectHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientConnectionEventHandler {

    @SubscribeEvent
    public void onDc(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        AstralAltar.Log("Cleaning client cache...");
        NewEffectHandler.cleanUp();
    }
}
