package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostAcacia extends BlockSignPost {

    public BlockSignPostAcacia() {
        this(0);
    }

    public BlockSignPostAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.ACACIA_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
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
