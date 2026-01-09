package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedGreen extends BlockTerracottaGlazed {

    BlockTerracottaGlazedGreen() {

    }

    @Override
    public int getId() {
        return GREEN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Green Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
