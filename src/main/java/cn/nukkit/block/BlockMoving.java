package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class BlockMoving extends BlockTransparent {

    BlockMoving() {

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

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getResistance() {
        return 18000000;
    }
}
