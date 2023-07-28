package cn.nukkit.level.biome.impl.mountain;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;

public class StonyPeaksBiome extends CoveredBiome {
    public StonyPeaksBiome() {
        super();

        this.addPopulator(new PopulatorOre(
                new OreType(Block.get(BlockID.EMERALD_ORE), 11, 1, 0, 32),
                new OreType(Block.get(BlockID.MONSTER_EGG), 7, 9, 0, 63)));

        this.setBaseHeight(1.5f);
        this.setHeightVariation(0.5f);
    }

    @Override
    public String getName() {
        return "Stony Peaks";
    }

    @Override
    public int getSurfaceDepth(int y) {
        return 0;
    }

    @Override
    public int getSurfaceBlock(int y) {
        return 0;
    }

    @Override
    public int getGroundDepth(int y) {
        return 0;
    }

    @Override
    public int getGroundBlock(int y) {
        return 0;
    }
}
