package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignCrimson extends BlockWallSign {
    BlockWallSignCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Crimson Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CRIMSON_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }

    @Override
    protected int getStandingBlockId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return CRIMSON_WALL_SIGN;
    }
}
