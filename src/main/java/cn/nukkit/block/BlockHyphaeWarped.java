package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeWarped extends BlockHyphae {
    public BlockHyphaeWarped() {
        this(0);
    }

    public BlockHyphaeWarped(int meta) {
        super(meta);
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
