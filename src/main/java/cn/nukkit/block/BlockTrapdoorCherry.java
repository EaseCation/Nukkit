package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorCherry extends BlockTrapdoor {
    BlockTrapdoorCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Cherry Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
