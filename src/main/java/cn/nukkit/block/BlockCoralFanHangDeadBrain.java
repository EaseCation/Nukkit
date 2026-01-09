package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockCoralFanHangDeadBrain extends BlockCoralFanHangDead {
    BlockCoralFanHangDeadBrain() {

    }

    @Override
    public int getId() {
        return DEAD_BRAIN_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Brain Coral Wall Fan";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DEAD_BRAIN_CORAL_FAN);
    }
}
