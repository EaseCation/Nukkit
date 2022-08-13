package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockSignPostJungle extends BlockSignPost {

    public BlockSignPostJungle() {
        this(0);
    }

    public BlockSignPostJungle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Jungle Oak Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.JUNGLE_SIGN);
    }

    @Override
    protected int getStandingBlockId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return JUNGLE_WALL_SIGN;
    }
}
