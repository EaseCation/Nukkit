package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.level.generator.populator.impl.tree.SpruceBigTreePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class MegaSpruceTaigaBiome extends TaigaBiome {
    public MegaSpruceTaigaBiome() {
        super();

        SpruceBigTreePopulator bigTrees = new SpruceBigTreePopulator();
        bigTrees.setBaseAmount(6);
        this.addPopulator(bigTrees);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.2f);
    }

    @Override
    public String getName() {
        return "Mega Spruce Taiga";
    }
}
