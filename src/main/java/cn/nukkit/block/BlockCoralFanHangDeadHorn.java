package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockCoralFanHangDeadHorn extends BlockCoralFanHangDead {
    BlockCoralFanHangDeadHorn() {

    }

    @Override
    public int getId() {
        return DEAD_HORN_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Horn Coral Wall Fan";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DEAD_HORN_CORAL_FAN);
    }
}
