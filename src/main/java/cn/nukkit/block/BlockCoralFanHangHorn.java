package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHangHorn extends BlockCoralFanHang {
    BlockCoralFanHangHorn() {

    }

    @Override
    public int getId() {
        return HORN_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Horn Coral Wall Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.HORN_CORAL_FAN);
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_HORN_CORAL_WALL_FAN;
    }
}
