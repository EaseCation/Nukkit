package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNyliumWarped extends BlockNylium {
    BlockNyliumWarped() {

    }

    @Override
    public int getId() {
        return WARPED_NYLIUM;
    }

    @Override
    public String getName() {
        return "Warped Nylium";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }

    @Override
    protected void placeVegetation() {
        scatterVegetation(random -> {
            int num = random.nextInt(100);
            if (num == 0) {
                return get(CRIMSON_FUNGUS);
            }
            if (num == 1) {
                return get(CRIMSON_ROOTS);
            }
            if (num > 14) {
                return get(WARPED_ROOTS);
            }
            return get(WARPED_FUNGUS);
        });

        scatterVegetation(random -> get(NETHER_SPROUTS));
    }
}
