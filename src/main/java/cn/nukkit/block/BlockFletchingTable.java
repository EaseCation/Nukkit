package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFletchingTable extends BlockSolid {

    BlockFletchingTable() {

    }

    @Override
    public int getId() {
        return FLETCHING_TABLE;
    }

    @Override
    public String getName() {
        return "Fletching Table";
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public float getHardness() {
        return 2.5f;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
