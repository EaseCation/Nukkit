package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedRed extends BlockTerracottaGlazed {

    BlockTerracottaGlazedRed() {

    }

    @Override
    public int getId() {
        return RED_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Red Glazed Terracotta";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
