package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockSignPostWarped extends BlockSignPost {
    public BlockSignPostWarped() {
        this(0);
    }

    public BlockSignPostWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Warped Sign";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.WARPED_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }

    @Override
    protected int getStandingBlockId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    protected int getWallBlockId() {
        return WARPED_WALL_SIGN;
    }
}
