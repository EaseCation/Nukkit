package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedGray extends BlockTerracottaGlazed {

    BlockTerracottaGlazedGray() {

    }

    @Override
    public int getId() {
        return GRAY_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Gray Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
