package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public abstract class BlockConcrete extends BlockSolid {
    public static final int[] CONCRETES = {
            WHITE_CONCRETE,
            ORANGE_CONCRETE,
            MAGENTA_CONCRETE,
            LIGHT_BLUE_CONCRETE,
            YELLOW_CONCRETE,
            LIME_CONCRETE,
            PINK_CONCRETE,
            GRAY_CONCRETE,
            LIGHT_GRAY_CONCRETE,
            CYAN_CONCRETE,
            PURPLE_CONCRETE,
            BLUE_CONCRETE,
            BROWN_CONCRETE,
            GREEN_CONCRETE,
            RED_CONCRETE,
            BLACK_CONCRETE,
    };

    @Override
    public float getResistance() {
        return 9;
    }

    @Override
    public float getHardness() {
        return 1.8f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return item.getTier() >= ItemTool.TIER_WOODEN ? new Item[]{toItem(true)} : new Item[0];
    }

    @Override
    public boolean isConcrete() {
        return true;
    }

    public abstract DyeColor getDyeColor();
}
