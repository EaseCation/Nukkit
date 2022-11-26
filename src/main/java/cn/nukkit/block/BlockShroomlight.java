package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockShroomlight extends BlockSolid {
    public BlockShroomlight() {
    }

    @Override
    public String getName() {
        return "Shroomlight";
    }

    @Override
    public int getId() {
        return SHROOMLIGHT;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
