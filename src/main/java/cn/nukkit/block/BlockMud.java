package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMud extends BlockSolid {
    BlockMud() {

    }

    @Override
    public int getId() {
        return MUD;
    }

    @Override
    public String getName() {
        return "Mud";
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 0.125;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public boolean isDirt() {
        return true;
    }
}
