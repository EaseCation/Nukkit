package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHangBrain extends BlockCoralFanHang {
    BlockCoralFanHangBrain() {

    }

    @Override
    public int getId() {
        return BRAIN_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Brain Coral Wall Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.BRAIN_CORAL_FAN);
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_BRAIN_CORAL_WALL_FAN;
    }
}
