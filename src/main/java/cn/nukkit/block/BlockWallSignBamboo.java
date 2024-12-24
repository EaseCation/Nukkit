package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignBamboo extends BlockWallSign {
    public BlockWallSignBamboo() {
        this(0);
    }

    public BlockWallSignBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Bamboo Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BAMBOO_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return BAMBOO_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return BAMBOO_WALL_SIGN;
    }
}
