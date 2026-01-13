package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksJungle extends BlockPlanks {
    BlockPlanksJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_PLANKS;
    }

    @Override
    public String getName() {
        return "Jungle Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.planks.jungle.name";
    }
}
