package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockBorder extends BlockWall {

    public BlockBorder() {
        this(0);
    }

    public BlockBorder(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BORDER_BLOCK;
    }

    @Override
    public String getName() {
        return "Border";
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 18000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FIRE_BLOCK_COLOR;
    }
}
