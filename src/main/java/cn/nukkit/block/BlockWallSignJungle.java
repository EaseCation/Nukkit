package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignJungle extends BlockWallSign {

    BlockWallSignJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Jungle Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.JUNGLE_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return JUNGLE_WALL_SIGN;
    }
}
