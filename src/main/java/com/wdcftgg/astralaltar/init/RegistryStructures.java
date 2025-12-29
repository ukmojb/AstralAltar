package com.wdcftgg.astralaltar.init;

import com.wdcftgg.astralaltar.init.multiblock.MultiblockAltarGod;
import hellfirepvp.astralsorcery.common.structure.StructureMatcherRegistry;
import hellfirepvp.astralsorcery.common.structure.StructureRegistry;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;

public class RegistryStructures {
    public static PatternBlockArray patternAltarGod;

    public static void init() {
        patternAltarGod = registerPattern(new MultiblockAltarGod());
    }

    private static <T extends PatternBlockArray> T registerPattern(T pattern) {
        StructureRegistry.INSTANCE.register(pattern);
        StructureMatcherRegistry.INSTANCE.register(() -> new StructureMatcherPatternArray(pattern.getRegistryName()));
        return pattern;
    }
}
