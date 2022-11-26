package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostDarkOak extends BlockSignPost {

    public BlockSignPostDarkOak() {
        this(0);
    }

    public BlockSignPostDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARKOAK_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Dark Oak Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.DARK_OAK_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return DARKOAK_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return DARKOAK_WALL_SIGN;
    }
}
