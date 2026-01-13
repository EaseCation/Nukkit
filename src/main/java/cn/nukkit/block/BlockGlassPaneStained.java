package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public abstract class BlockGlassPaneStained extends BlockGlassPane {
    public static final int[] STAINED_GLASS_PANES = {
            WHITE_STAINED_GLASS_PANE,
            ORANGE_STAINED_GLASS_PANE,
            MAGENTA_STAINED_GLASS_PANE,
            LIGHT_BLUE_STAINED_GLASS_PANE,
            YELLOW_STAINED_GLASS_PANE,
            LIME_STAINED_GLASS_PANE,
            PINK_STAINED_GLASS_PANE,
            GRAY_STAINED_GLASS_PANE,
            LIGHT_GRAY_STAINED_GLASS_PANE,
            CYAN_STAINED_GLASS_PANE,
            PURPLE_STAINED_GLASS_PANE,
            BLUE_STAINED_GLASS_PANE,
            BROWN_STAINED_GLASS_PANE,
            GREEN_STAINED_GLASS_PANE,
            RED_STAINED_GLASS_PANE,
            BLACK_STAINED_GLASS_PANE,
    };

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public boolean isStainedGlassPane() {
        return true;
    }

    public abstract DyeColor getDyeColor();

    @Override
    public String getDescriptionId() {
        return "tile.stained_glass_pane." + getDyeColor().getDescriptionNameSnakeCase() + ".name";
    }
}
