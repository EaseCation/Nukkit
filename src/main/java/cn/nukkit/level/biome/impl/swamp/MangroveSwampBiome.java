package cn.nukkit.level.biome.impl.swamp;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorLilyPad;
import cn.nukkit.level.generator.populator.impl.tree.SwampTreePopulator;

import static cn.nukkit.GameVersion.*;

public class MangroveSwampBiome extends GrassyBiome {
    public MangroveSwampBiome() {
        super();

        PopulatorLilyPad lilypad = new PopulatorLilyPad();
        lilypad.setBaseAmount(4);
        lilypad.setRandomAmount(2);
        this.addPopulator(lilypad);

        //TODO: mangrove
        SwampTreePopulator trees = new SwampTreePopulator();
        trees.setBaseAmount(2);
        this.addPopulator(trees);

        this.setBaseHeight(-0.2f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Mangrove Swamp";
    }

    @Override
    public int getSurfaceBlock(int y) {
        if (!V1_19_0.isAvailable()) {
            return super.getSurfaceBlock(y);
        }
        return MUD;
    }

    @Override
    public int getGroundBlock(int y) {
        if (!V1_19_0.isAvailable()) {
            return super.getGroundBlock(y);
        }
        return MUD;
    }
}
