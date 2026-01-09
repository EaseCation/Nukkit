package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeWarped extends BlockHyphae {
    BlockHyphaeWarped() {

    }

    @Override
    public int getId() {
        return WARPED_HYPHAE;
    }

    @Override
    public String getName() {
        return "Warped Hyphae";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_HYPHAE_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_WARPED_HYPHAE, getDamage());
    }
}
