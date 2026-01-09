package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockResinClump extends BlockMultiface {
    BlockResinClump() {

    }

    @Override
    public int getId() {
        return RESIN_CLUMP;
    }

    @Override
    public String getName() {
        return "Resin Clump";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR;
    }
}
