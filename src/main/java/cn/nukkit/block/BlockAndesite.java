package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockAndesite extends BlockStoneAbstract {
    BlockAndesite() {

    }

    @Override
    public int getId() {
        return ANDESITE;
    }

    @Override
    public String getName() {
        return "Andesite";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone.andesite.name";
    }
}
