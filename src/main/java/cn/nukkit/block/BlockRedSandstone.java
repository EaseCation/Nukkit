package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockRedSandstone extends BlockSandstone {

    public static final int[] RED_SANDSTONES = new int[]{
            RED_SANDSTONE,
            CHISELED_RED_SANDSTONE,
            CUT_RED_SANDSTONE,
            SMOOTH_RED_SANDSTONE,
    };
    @SuppressWarnings("unused")
    private static final int[] SANDSTONES = RED_SANDSTONES;

    BlockRedSandstone() {

    }

    @Override
    public int getId() {
        return RED_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Red Sandstone";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_sandstone.default.name";
    }
}
