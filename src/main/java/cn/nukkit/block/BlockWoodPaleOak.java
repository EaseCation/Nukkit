package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodPaleOak extends BlockLogPaleOak {
    public BlockWoodPaleOak() {
        this(0);
    }

    public BlockWoodPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Pale Oak Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_PALE_OAK_WOOD, getDamage());
    }
}
