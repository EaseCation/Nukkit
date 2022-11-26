package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.LevelException;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockThin extends BlockTransparent {

    protected BlockThin() {
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        final double offNW = 7.0 / 16.0;
        final double offSE = 9.0 / 16.0;
        final double onNW = 0.0;
        final double onSE = 1.0;
        double w = offNW;
        double e = offSE;
        double n = offNW;
        double s = offSE;
        try {
            boolean north = this.canConnect(this.north(), BlockFace.SOUTH);
            boolean south = this.canConnect(this.south(), BlockFace.NORTH);
            boolean west = this.canConnect(this.west(), BlockFace.EAST);
            boolean east = this.canConnect(this.east(), BlockFace.WEST);
            w = west ? onNW : offNW;
            e = east ? onSE : offSE;
            n = north ? onNW : offNW;
            s = south ? onSE : offSE;
        } catch (LevelException ignore) {
            //null sucks
        }
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1,
                this.z + s
        );
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    public boolean canConnect(Block block, BlockFace face) {
        return block instanceof BlockThin || block.isWall() || SupportType.hasFullSupport(block, face);
    }

}
