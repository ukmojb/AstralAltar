package com.wdcftgg.astralaltar.init.multiblock;

import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class NewStructureRegistry {

    private static Map<String, PatternBlockArray> newStructureMap = new HashMap<>();

    public static void initNewStructure(String entity, PatternBlockArray patternBlockArray) {
        newStructureMap.put(entity, patternBlockArray);
    }

    public static boolean hasTileEntity(TileEntity entity) {
        for (String rl : newStructureMap.keySet()) {
            if (rl.equals(TileEntity.getKey(entity.getClass()).toString())) {
                return true;
            }
        }
        return false;
    }

    public static PatternBlockArray getPatternBlockArray(TileEntity entity) {
        if (!hasTileEntity(entity)) return null;

        return newStructureMap.get(TileEntity.getKey(entity.getClass()).toString());
    }


}
