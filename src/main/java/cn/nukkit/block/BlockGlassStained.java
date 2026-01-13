package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public abstract class BlockGlassStained extends BlockGlass {
    public static final int[] STAINED_GLASSES = {
            WHITE_STAINED_GLASS,
            ORANGE_STAINED_GLASS,
            MAGENTA_STAINED_GLASS,
            LIGHT_BLUE_STAINED_GLASS,
            YELLOW_STAINED_GLASS,
            LIME_STAINED_GLASS,
            PINK_STAINED_GLASS,
            GRAY_STAINED_GLASS,
            LIGHT_GRAY_STAINED_GLASS,
            CYAN_STAINED_GLASS,
            PURPLE_STAINED_GLASS,
            BLUE_STAINED_GLASS,
            BROWN_STAINED_GLASS,
            GREEN_STAINED_GLASS,
            RED_STAINED_GLASS,
            BLACK_STAINED_GLASS,
    };

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0;
    }

    @Override
    public boolean isStainedGlass() {
        return true;
    }

    public abstract DyeColor getDyeColor();

    @Override
    public String getDescriptionId() {
        return "tile.stained_glass." + getDyeColor().getDescriptionNameSnakeCase() + ".name";
    }
}
