package cn.nukkit.block;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsStoneBrick extends BlockStairs {
    public BlockStairsStoneBrick() {
        this(0);
    }

    public BlockStairsStoneBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_BRICK_STAIRS;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
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
    public String getName() {
        return "Stone Brick Stairs";
    }
}
