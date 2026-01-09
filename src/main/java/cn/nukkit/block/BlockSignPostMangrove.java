package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostMangrove extends BlockSignPost {
    BlockSignPostMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.MANGROVE_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return MANGROVE_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return MANGROVE_WALL_SIGN;
    }
}
