package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHang3 extends BlockCoralFanHang {

    public static final int YELLOW = 0;

    public BlockCoralFanHang3() {
        this(0);
    }

    public BlockCoralFanHang3(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN_HANG3;
    }

    @Override
    public String getName() {
        return "Hang Horn Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(isDead() ? CORAL_FAN_DEAD : CORAL_FAN), BlockCoralFan.YELLOW);
    }
}
