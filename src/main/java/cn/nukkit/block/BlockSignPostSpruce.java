package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockSignPostSpruce extends BlockSignPost {

    public BlockSignPostSpruce() {
        this(0);
    }

    public BlockSignPostSpruce(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Spruce Oak Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.SPRUCE_SIGN);
    }

    @Override
    protected int getStandingBlockId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return SPRUCE_WALL_SIGN;
    }
}
