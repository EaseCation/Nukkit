package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockFence extends BlockTransparent {
    public static final int[] FENCES = {
            OAK_FENCE,
            SPRUCE_FENCE,
            BIRCH_FENCE,
            JUNGLE_FENCE,
            ACACIA_FENCE,
            DARK_OAK_FENCE,
    };

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.north(), BlockFace.SOUTH);
        boolean south = this.canConnect(this.south(), BlockFace.NORTH);
        boolean west = this.canConnect(this.west(), BlockFace.EAST);
        boolean east = this.canConnect(this.east(), BlockFace.WEST);
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    public boolean canConnect(Block block, BlockFace face) {
        return block.isFence() && block.getId() != NETHER_BRICK_FENCE || block.isFenceGate() || SupportType.hasFullSupport(block, face);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face.isVertical() && type == SupportType.CENTER;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isFence() {
        return true;
    }
}
