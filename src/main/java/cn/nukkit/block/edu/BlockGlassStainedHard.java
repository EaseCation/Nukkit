package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlassStained;
import cn.nukkit.item.Item;

public abstract class BlockGlassStainedHard extends BlockGlassStained {
    public static final int[] HARD_STAINED_GLASSES = {
            HARD_WHITE_STAINED_GLASS,
            HARD_ORANGE_STAINED_GLASS,
            HARD_MAGENTA_STAINED_GLASS,
            HARD_LIGHT_BLUE_STAINED_GLASS,
            HARD_YELLOW_STAINED_GLASS,
            HARD_LIME_STAINED_GLASS,
            HARD_PINK_STAINED_GLASS,
            HARD_GRAY_STAINED_GLASS,
            HARD_LIGHT_GRAY_STAINED_GLASS,
            HARD_CYAN_STAINED_GLASS,
            HARD_PURPLE_STAINED_GLASS,
            HARD_BLUE_STAINED_GLASS,
            HARD_BROWN_STAINED_GLASS,
            HARD_GREEN_STAINED_GLASS,
            HARD_RED_STAINED_GLASS,
            HARD_BLACK_STAINED_GLASS,
    };
    @SuppressWarnings("unused")
    private static final int[] STAINED_GLASSES = HARD_STAINED_GLASSES;

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
}
