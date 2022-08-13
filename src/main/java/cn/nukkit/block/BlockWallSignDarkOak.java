package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockWallSignDarkOak extends BlockWallSign {

    public BlockWallSignDarkOak() {
        this(0);
    }

    public BlockWallSignDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARKOAK_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Dark Oak Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.DARK_OAK_SIGN);
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
