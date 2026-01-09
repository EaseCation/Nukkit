package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignCherry extends BlockWallSign {
    BlockWallSignCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Cherry Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CHERRY_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return CHERRY_WALL_SIGN;
    }
}
