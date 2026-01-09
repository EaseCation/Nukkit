package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignAcacia extends BlockWallSign {

    BlockWallSignAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.ACACIA_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return ACACIA_WALL_SIGN;
    }
}
