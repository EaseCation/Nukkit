package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostCherry extends BlockSignPost {
    public BlockSignPostCherry() {
        this(0);
    }

    public BlockSignPostCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Cherry Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CHERRY_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getStandingBlockId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return CHERRY_WALL_SIGN;
    }
}
