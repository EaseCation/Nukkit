package cn.nukkit.level.biome.impl.mountain;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

public class MeadowBiome extends GrassyBiome {
    public MeadowBiome() {
        super();

        PopulatorTree oak = new PopulatorTree(BlockSapling.OAK);
        oak.setBaseAmount(1);
        oak.setRandomAmount(1);
        this.addPopulator(oak);

        PopulatorTree birch = new PopulatorTree(BlockSapling.BIRCH_TALL);
        birch.setBaseAmount(1);
        birch.setRandomAmount(1);
        this.addPopulator(birch);

        this.addPopulator(new PopulatorOre(STONE,
                new OreType(Block.get(BlockID.EMERALD_ORE), 11, 1, 0, 32),
                new OreType(Block.get(BlockID.MONSTER_EGG), 7, 9, 0, 63)));

        this.setBaseHeight(1);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Meadow";
    }
}
