package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedPink extends BlockTerracottaGlazed {

    BlockTerracottaGlazedPink() {

    }

    @Override
    public int getId() {
        return PINK_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Pink Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
