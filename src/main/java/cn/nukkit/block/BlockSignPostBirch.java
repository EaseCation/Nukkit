package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockSignPostBirch extends BlockSignPost {

    public BlockSignPostBirch() {
        this(0);
    }

    public BlockSignPostBirch(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Birch Oak Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.BIRCH_SIGN);
    }

    @Override
    protected int getStandingBlockId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return BIRCH_WALL_SIGN;
    }
}
