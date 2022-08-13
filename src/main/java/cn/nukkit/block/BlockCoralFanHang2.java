package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockCoralFanHang2 extends BlockCoralFanHang {

    public static final int PURPLE = 0;
    public static final int RED = 1;

    public BlockCoralFanHang2() {
        this(0);
    }

    public BlockCoralFanHang2(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN_HANG2;
    }

    @Override
    public String getName() {
        return getCoralType() == PURPLE ? "Hang Bubble Coral Fan" : "Hang Fire Coral Fan";
    }

    @Override
    public BlockColor getColor() {
        switch (getCoralType()) {
            default:
            case PURPLE:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case RED:
                return BlockColor.RED_BLOCK_COLOR;
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(isDead() ? CORAL_FAN_DEAD : CORAL_FAN), getCoralType() == PURPLE ? BlockCoralFan.PURPLE : BlockCoralFan.RED);
    }
}
