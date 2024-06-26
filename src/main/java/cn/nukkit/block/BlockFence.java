package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFence extends BlockTransparentMeta {

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    private static final String[] NAMES = new String[]{
            "Oak Fence",
            "Spruce Fence",
            "Birch Fence",
            "Jungle Fence",
            "Acacia Fence",
            "Dark Oak Fence",
            "Fence",
            "Fence",
    };

    public BlockFence() {
        this(0);
    }

    public BlockFence(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE;
    }

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
    public String getName() {
        return NAMES[this.getDamage() & 0x07];
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
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case BlockFence.OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case BlockFence.SPRUCE:
                return BlockColor.PODZOL_BLOCK_COLOR;
            case BlockFence.BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case BlockFence.JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
            case BlockFence.ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case BlockFence.DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
        }
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
