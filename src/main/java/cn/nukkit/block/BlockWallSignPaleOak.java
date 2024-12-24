package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignPaleOak extends BlockWallSign {
    public BlockWallSignPaleOak() {
        this(0);
    }

    public BlockWallSignPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Pale Oak Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.PALE_OAK_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return PALE_OAK_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return PALE_OAK_WALL_SIGN;
    }
}
