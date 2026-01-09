package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostBirch extends BlockSignPost {

    BlockSignPostBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Birch Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.BIRCH_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return BIRCH_WALL_SIGN;
    }
}
