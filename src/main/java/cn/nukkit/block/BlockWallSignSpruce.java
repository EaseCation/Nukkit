package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockWallSignSpruce extends BlockWallSign {

    public BlockWallSignSpruce() {
        this(0);
    }

    public BlockWallSignSpruce(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPRUCE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Spruce Wall Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.SPRUCE_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
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
