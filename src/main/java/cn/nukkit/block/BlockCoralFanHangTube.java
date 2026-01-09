package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHangTube extends BlockCoralFanHang {
    BlockCoralFanHangTube() {

    }

    @Override
    public int getId() {
        return TUBE_CORAL_WALL_FAN;
    }

    @Override
    public String getName() {
        return "Tube Coral Wall Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.TUBE_CORAL_FAN);
    }

    @Override
    protected int getDeadBlockId() {
        return DEAD_TUBE_CORAL_WALL_FAN;
    }
}
