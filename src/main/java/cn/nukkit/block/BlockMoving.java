package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;

public class BlockMoving extends BlockTransparent {

    public BlockMoving() {
        this(0);
    }

    public BlockMoving(int meta) {
        super();
    }

    @Override
    public String getName() {
        return "Moving Block";
    }

    @Override
    public int getId() {
        return BlockID.MOVING_BLOCK;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.MOVING_BLOCK;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
