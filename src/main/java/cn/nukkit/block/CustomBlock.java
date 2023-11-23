package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public abstract class CustomBlock extends Block {
    private final int id;

    protected CustomBlock(int id) {
        this.id = id;
    }

    @Override
    public final int getId() {
        return id;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return getHardness() * 5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

    @Override
    public final boolean isVanilla() {
        return false;
    }
}
