package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class FlowerForestBiome extends ForestBiome {
    public FlowerForestBiome() {
        this(TYPE_NORMAL);
    }

    public FlowerForestBiome(int type) {
        super(type);

        //see https://minecraft.gamepedia.com/Flower#Flower_biomes
        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(10);
        flower.addType(Block.DANDELION);
        flower.addType(Block.POPPY);
        flower.addType(Block.ALLIUM);
        flower.addType(Block.AZURE_BLUET);
        flower.addType(Block.RED_TULIP);
        flower.addType(Block.ORANGE_TULIP);
        flower.addType(Block.WHITE_TULIP);
        flower.addType(Block.PINK_TULIP);
        flower.addType(Block.OXEYE_DAISY);
        flower.addType(Block.CORNFLOWER);
        flower.addType(Block.LILY_OF_THE_VALLEY);
        flower.addType(Block.LILAC, true);
        flower.addType(Block.ROSE_BUSH, true);
        flower.addType(Block.PEONY, true);
        this.addPopulator(flower);

        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return this.type == TYPE_BIRCH ? "Birch Forest" : "Forest";
    }
}
