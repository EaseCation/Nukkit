package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockResin extends BlockSolid {
    public BlockResin() {
    }

    @Override
    public int getId() {
        return RESIN_BLOCK;
    }

    @Override
    public String getName() {
        return "Block of Resin";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR;
    }
}
