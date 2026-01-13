package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedBlue extends BlockTerracottaGlazed {

    BlockTerracottaGlazedBlue() {

    }

    @Override
    public int getId() {
        return BLUE_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Blue Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
