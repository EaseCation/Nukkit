package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsPurpur extends BlockStairs {

    public BlockStairsPurpur() {
        this(0);
    }

    public BlockStairsPurpur(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PURPUR_STAIRS;
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Purpur Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.MAGENTA_BLOCK_COLOR;
    }
}
