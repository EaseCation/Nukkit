package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockRedSandstone extends BlockSandstone {

    private static final String[] NAMES = new String[]{
            "Red Sandstone",
            "Chiseled Red Sandstone",
            "Cut Red Sandstone",
            "Smooth Red Sandstone",
    };

    public BlockRedSandstone() {
        this(0);
    }

    public BlockRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE;
    }

    @Override
    public String getName() {
        return NAMES[this.getSandstoneType()];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
