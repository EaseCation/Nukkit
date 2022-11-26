package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

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
        return "Jungle Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.JUNGLE_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
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
