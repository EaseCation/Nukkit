package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignWarped extends BlockWallSign {
    BlockWallSignWarped() {

    }

    @Override
    public int getId() {
        return WARPED_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Warped Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.WARPED_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }

    @Override
    protected int getStandingBlockId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return WARPED_WALL_SIGN;
    }
}
