package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockCoralFanHangDeadFire extends BlockCoralFanHangDead {
    BlockCoralFanHangDeadFire() {

    }

    @Override
    public int getId() {
        return DEAD_FIRE_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Fire Coral Wall Fan";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DEAD_FIRE_CORAL_FAN);
    }
}
