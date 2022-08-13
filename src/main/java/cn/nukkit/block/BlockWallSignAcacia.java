package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockWallSignAcacia extends BlockWallSign {

    public BlockWallSignAcacia() {
        this(0);
    }

    public BlockWallSignAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.ACACIA_SIGN);
    }

    @Override
    protected int getStandingBlockId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return ACACIA_WALL_SIGN;
    }
}
