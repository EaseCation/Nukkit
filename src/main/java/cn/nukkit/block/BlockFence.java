package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

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
        return recalculateBoundingBox(false);
    }

    private AxisAlignedBB recalculateBoundingBox(boolean clamp) {
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
                this.y + (clamp ? 1 : 1.5f),
                this.z + s
        );
    }

    @Override
    protected AxisAlignedBB recalculateClipBoundingBox() {
        return recalculateBoundingBox(true);
    }

    @Override
    public AxisAlignedBB[] getCollisionShape(int flags) {
        boolean north = canConnect(north(), BlockFace.SOUTH);
        boolean south = canConnect(south(), BlockFace.NORTH);
        boolean west = canConnect(west(), BlockFace.EAST);
        boolean east = canConnect(east(), BlockFace.WEST);

        float height = ClipFlag.has(flags, ClipFlag.CLAMP) ? 1 : 1.5f;
        List<AxisAlignedBB> aabbs = new ArrayList<>(2);

        if (north || south) {
            aabbs.add(new SimpleAxisAlignedBB(x + 6 / 16f, y, north ? z : z + 6 / 16f, x + 1 - 6 / 16f, y + height, south ? z + 1 : z + 1 - 6 / 16f));
        }

        if (west || east) {
            aabbs.add(new SimpleAxisAlignedBB(west ? x : x + 6 / 16f, y, z + 6 / 16f, east ? x + 1 : x + 1 - 6 / 16f, y + height, z + 1 - 6 / 16f));
        }

        return aabbs.isEmpty() ? new AxisAlignedBB[]{
                new SimpleAxisAlignedBB(x + 6 / 16f, y, z + 6 / 16f, x + 1 - 6 / 16f, y + height, z + 1 - 6 / 16f),
        } : aabbs.toArray(new AxisAlignedBB[0]);
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    protected boolean canConnect(Block block, BlockFace face) {
        if (block.is(BARRIER) || block.is(MELON_BLOCK) || block.isPumpkin()) {
            return false;
        }
        return block.isFence() && block.getId() != NETHER_BRICK_FENCE
                || block.isFenceGate() && ((BlockFenceGate) block).getBlockFace().getAxis() == face.rotateY().getAxis()
                || SupportType.hasFullSupport(block, face);
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
