package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHangFire extends BlockCoralFanHang {
    BlockCoralFanHangFire() {

    }

    @Override
    public int getId() {
        return FIRE_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Fire Coral Wall Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.FIRE_CORAL_FAN);
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_FIRE_CORAL_WALL_FAN;
    }
}
