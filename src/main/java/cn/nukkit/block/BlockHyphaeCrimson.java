package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeCrimson extends BlockHyphae {
    BlockHyphaeCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_HYPHAE;
    }

    @Override
    public String getName() {
        return "Crimson Hyphae";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_CRIMSON_HYPHAE, getDamage());
    }
}

