package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockLeafLitter extends BlockSegmentable {
    BlockLeafLitter() {

    }

    @Override
    public int getId() {
        return LEAF_LITTER;
    }

    @Override
    public String getName() {
        return "Leaf Litter";
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isFertilizable() {
        return false;
    }

    @Override
    public int getFuelTime() {
        return 100;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    protected boolean canSurvive() {
        return SupportType.hasFullSupport(down(), BlockFace.UP);
    }
}
