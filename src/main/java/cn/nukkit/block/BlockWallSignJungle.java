package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockWallSignJungle extends BlockWallSign {

    public BlockWallSignJungle() {
        this(0);
    }

    public BlockWallSignJungle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JUNGLE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Jungle Wall Sign";
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
