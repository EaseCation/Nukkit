package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsWood extends BlockStairs {
    public BlockStairsWood() {
        this(0);
    }

    public BlockStairsWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OAK_STAIRS;
    }

    @Override
    public String getName() {
        return "Oak Stairs";
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
         return new Item[]{
                toItem(true)
         };
    }
}
