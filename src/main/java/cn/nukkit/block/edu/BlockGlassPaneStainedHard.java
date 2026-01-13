package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlassPaneStained;
import cn.nukkit.item.Item;

public abstract class BlockGlassPaneStainedHard extends BlockGlassPaneStained {
    public static final int[] HARD_STAINED_GLASS_PANES = {
            HARD_WHITE_STAINED_GLASS_PANE,
            HARD_ORANGE_STAINED_GLASS_PANE,
            HARD_MAGENTA_STAINED_GLASS_PANE,
            HARD_LIGHT_BLUE_STAINED_GLASS_PANE,
            HARD_YELLOW_STAINED_GLASS_PANE,
            HARD_LIME_STAINED_GLASS_PANE,
            HARD_PINK_STAINED_GLASS_PANE,
            HARD_GRAY_STAINED_GLASS_PANE,
            HARD_LIGHT_GRAY_STAINED_GLASS_PANE,
            HARD_CYAN_STAINED_GLASS_PANE,
            HARD_PURPLE_STAINED_GLASS_PANE,
            HARD_BLUE_STAINED_GLASS_PANE,
            HARD_BROWN_STAINED_GLASS_PANE,
            HARD_GREEN_STAINED_GLASS_PANE,
            HARD_RED_STAINED_GLASS_PANE,
            HARD_BLACK_STAINED_GLASS_PANE,
    };
    @SuppressWarnings("unused")
    private static final int[] STAINED_GLASS_PANES = HARD_STAINED_GLASS_PANES;

    @Override
    public float getHardness() {
        return 10;
    }

    @Override
    public float getResistance() {
        return 50;
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                this.toItem(true)
        };
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "tile.hard_stained_glass_pane." + getDyeColor().getDescriptionNameSnakeCase() + ".name";
    }
}
