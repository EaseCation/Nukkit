package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedYellow extends BlockTerracottaGlazed {

    BlockTerracottaGlazedYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Yellow Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
