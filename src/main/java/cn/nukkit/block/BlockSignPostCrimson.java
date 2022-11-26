package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostCrimson extends BlockSignPost {
    public BlockSignPostCrimson() {
        this(0);
    }

    public BlockSignPostCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Crimson Sign";
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
    protected int getStandingBlockId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return CRIMSON_WALL_SIGN;
    }
}
