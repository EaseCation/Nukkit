package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedBlack extends BlockTerracottaGlazed {

    BlockTerracottaGlazedBlack() {

    }

    @Override
    public int getId() {
        return BLACK_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Black Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
