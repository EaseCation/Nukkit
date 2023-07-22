package cn.nukkit.level.biome.impl.mountain;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.SnowyBiome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

public class SnowySlopesBiome extends SnowyBiome {
    public SnowySlopesBiome() {
        super();

        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);

        this.addPopulator(new PopulatorOre(STONE,
                new OreType(Block.get(BlockID.EMERALD_ORE), 11, 1, 0, 32),
                new OreType(Block.get(BlockID.MONSTER_EGG), 7, 9, 0, 63)));

        this.setBaseHeight(1.25f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Snowy Slopes";
    }

    @Override
    public int getSurfaceBlock(int y) {
        return SNOW;
    }

    @Override
    public int getGroundBlock(int y) {
        return SNOW;
    }

    @Override
    public boolean isFreezing() {
        return true;
    }
}
