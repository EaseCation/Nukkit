package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNyliumCrimson extends BlockNylium {
    BlockNyliumCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_NYLIUM;
    }

    @Override
    public String getName() {
        return "Crimson Nylium";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_NYLIUM_BLOCK_COLOR;
    }

    @Override
    protected void placeVegetation() {
        scatterVegetation(random -> {
            int num = random.nextInt(100);
            if (num == 0) {
                return get(WARPED_FUNGUS);
            }
            if (num > 12) {
                return get(CRIMSON_ROOTS);
            }
            return get(CRIMSON_FUNGUS);
        });
    }
}
