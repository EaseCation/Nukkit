package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsMudBrick extends BlockStairs {
    public BlockStairsMudBrick() {
        this(0);
    }

    public BlockStairsMudBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MUD_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Mud Brick Stairs";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
