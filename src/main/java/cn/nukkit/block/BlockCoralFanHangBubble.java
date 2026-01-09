package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHangBubble extends BlockCoralFanHang {
    BlockCoralFanHangBubble() {

    }

    @Override
    public int getId() {
        return BUBBLE_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Bubble Coral Wall Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.BUBBLE_CORAL_FAN);
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BUBBLE_CORAL_WALL_FAN;
    }
}
