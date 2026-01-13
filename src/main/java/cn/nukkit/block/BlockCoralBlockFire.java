package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralBlockFire extends BlockCoralBlock {
    BlockCoralBlockFire() {

    }

    @Override
    public int getId() {
        return FIRE_CORAL_BLOCK;
    }

    @Override
    public String getName() {
        return "Fire Coral Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_FIRE_CORAL_BLOCK;
    }

    @Override
    public String getDescriptionId() {
        return "tile.coral_block.red.name";
    }
}
