package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWildflowers extends BlockPinkPetals {
    public BlockWildflowers() {
        super(0);
    }

    @Override
    public int getId() {
        return WILDFLOWERS;
    }

    @Override
    public String getName() {
        return "Wildflowers";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }
}
