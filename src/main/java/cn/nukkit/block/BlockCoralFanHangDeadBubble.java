package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockCoralFanHangDeadBubble extends BlockCoralFanHangDead {
    BlockCoralFanHangDeadBubble() {

    }

    @Override
    public int getId() {
        return DEAD_BUBBLE_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Bubble Coral Wall Fan";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DEAD_BUBBLE_CORAL_FAN);
    }
}
