package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignMangrove extends BlockWallSign {
    public BlockWallSignMangrove() {
        this(0);
    }

    public BlockWallSignMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Wall Sign";
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
