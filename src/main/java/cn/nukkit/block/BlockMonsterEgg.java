package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public abstract class BlockMonsterEgg extends BlockSolid {
    public static final int[] MONSTER_EGGS = {
            INFESTED_STONE,
            INFESTED_COBBLESTONE,
            INFESTED_STONE_BRICKS,
            INFESTED_MOSSY_STONE_BRICKS,
            INFESTED_CRACKED_STONE_BRICKS,
            INFESTED_CHISELED_STONE_BRICKS,
    };

    public static final int TYPE_STONE = 0;
    public static final int TYPE_COBBLESTONE = 1;
    public static final int TYPE_STONE_BRICK = 2;
    public static final int TYPE_MOSSY_BRICK = 3;
    public static final int TYPE_CRACKED_BRICK = 4;
    public static final int TYPE_CHISELED_BRICK = 5;

    @Override
    public float getHardness() {
        return 0.75f;
    }

    @Override
    public float getResistance() {
        return 3.75f;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
