package cn.nukkit.block;

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
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }
}
