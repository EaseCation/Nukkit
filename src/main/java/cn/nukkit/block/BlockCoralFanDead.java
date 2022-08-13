package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCoralFanDead extends BlockCoralFan {

    public BlockCoralFanDead() {
        this(0);
    }

    public BlockCoralFanDead(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_FAN_DEAD;
    }

    @Override
    public String getName() {
        return "Dead " + super.getName();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    protected boolean needCheckAlive() {
        return false;
    }

    @Override
    protected Block getHangBlock(int direction) {
        switch (getCoralColor()) {
            default:
            case BLUE:
                return Block.get(CORAL_FAN_HANG, BlockCoralFanHang.BLUE | BlockCoralFanHang.DEAD_BIT | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case PINK:
                return Block.get(CORAL_FAN_HANG, BlockCoralFanHang.PINK | BlockCoralFanHang.DEAD_BIT | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case PURPLE:
                return Block.get(CORAL_FAN_HANG2, BlockCoralFanHang2.PURPLE | BlockCoralFanHang.DEAD_BIT | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case RED:
                return Block.get(CORAL_FAN_HANG2, BlockCoralFanHang2.RED | BlockCoralFanHang.DEAD_BIT | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
            case YELLOW:
                return Block.get(CORAL_FAN_HANG3, BlockCoralFanHang3.YELLOW | BlockCoralFanHang.DEAD_BIT | (direction << BlockCoralFanHang.TYPE_DEAD_BITS));
        }
    }
}
