package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockCoralFanHangDeadTube extends BlockCoralFanHangDead {
    BlockCoralFanHangDeadTube() {

    }

    @Override
    public int getId() {
        return DEAD_TUBE_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Dead Tube Coral Wall Fan";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.DEAD_TUBE_CORAL_FAN);
    }
}
