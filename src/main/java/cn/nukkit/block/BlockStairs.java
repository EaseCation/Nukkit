package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.state.enumeration.MinecraftCornerState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockStairs extends BlockTransparent implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int UPSIDE_DOWN_BIT = 0b100;

    BlockStairs() {

    }

    @Override
    public double getMinY() {
        // TODO: this seems wrong
        return this.y + (isUpsideDown() ? 0.5 : 0);
    }

    @Override
    public double getMaxY() {
        // TODO: this seems wrong
        return this.y + (isUpsideDown() ? 1 : 0.5);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(player != null ? player.getDirection().getReversedHorizontalIndex() : 0);
        if ((fy > 0.5 && face != BlockFace.UP) || face == BlockFace.DOWN) {
            this.setDamage(this.getDamage() | UPSIDE_DOWN_BIT); //Upside-down stairs
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public AxisAlignedBB[] getCollisionShape(int flags) {
        boolean upsideDown = isUpsideDown();
        List<AxisAlignedBB> aabbs = new ArrayList<>(3);

        // base
        if (upsideDown) {
            aabbs.add(new SimpleAxisAlignedBB(x, y + 0.5f, z, x + 1, y + 1, z + 1));
        } else {
            aabbs.add(new SimpleAxisAlignedBB(x, y, z, x + 1, y + 0.5f, z + 1));
        }

        // step
        float bottom = 0.5f;
        float top = 1;
        if (upsideDown) {
            bottom = 0;
            top = 0.5f;
        }
        MinecraftCornerState corner = getCorner();
        switch (getBlockFace()) {
            case EAST -> {
                switch (corner) {
                    case NONE -> aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 1));
                    case INNER_LEFT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 1));
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 1, y + top, z + 0.5f)); // inner piece
                    }
                    case INNER_RIGHT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 1));
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                    }
                    case OUTER_LEFT -> aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 0.5f));
                    case OUTER_RIGHT -> aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                }
            }
            case WEST -> {
                switch (corner) {
                    case NONE -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 1));
                    case INNER_LEFT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 1));
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                    }
                    case INNER_RIGHT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 1));
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 1, y + top, z + 0.5f));
                    }
                    case OUTER_LEFT -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 0.5f, y + top, z + 1));
                    case OUTER_RIGHT -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 0.5f));
                }
            }
            case SOUTH -> {
                switch (corner) {
                    case NONE -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                    case INNER_LEFT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                        aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 1));
                    }
                    case INNER_RIGHT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 1));
                    }
                    case OUTER_LEFT -> aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z + 0.5f, x + 1, y + top, z + 1));
                    case OUTER_RIGHT -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z + 0.5f, x + 0.5f, y + top, z + 1));
                }
            }
            case NORTH -> {
                switch (corner) {
                    case NONE -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 1, y + top, z + 0.5f));
                    case INNER_LEFT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 1, y + top, z + 0.5f));
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 1));
                    }
                    case INNER_RIGHT -> {
                        aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 1, y + top, z + 0.5f));
                        aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 1));
                    }
                    case OUTER_LEFT -> aabbs.add(new SimpleAxisAlignedBB(x, y + bottom, z, x + 0.5f, y + top, z + 0.5f));
                    case OUTER_RIGHT -> aabbs.add(new SimpleAxisAlignedBB(x + 0.5f, y + bottom, z, x + 1, y + top, z + 0.5f));
                }
            }
        }

        return aabbs.toArray(new AxisAlignedBB[0]);
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public BlockFace getBlockFace() {
        return getBlockFace(this);
    }

    private static BlockFace getBlockFace(Block block) {
        return BlockFace.fromReversedHorizontalIndex(block.getDamage() & DIRECTION_MASK);
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
        switch (face) {
            case UP:
                return isUpsideDown();
            case DOWN:
                return !isUpsideDown();
        }
        return face == getBlockFace();
    }

    public boolean isUpsideDown() {
        return isUpsideDown(this);
    }

    private static boolean isUpsideDown(Block block) {
        return (block.getDamage() & UPSIDE_DOWN_BIT) != 0;
    }

    @Override
    public boolean isStairs() {
        return true;
    }

    public MinecraftCornerState getCorner() {
        BlockFace facing = getBlockFace();
        boolean upsideDown = isUpsideDown();

        Block back = getSide(facing);
        if (back.isStairs() && isUpsideDown(back) == upsideDown) {
            BlockFace face = getBlockFace(back);
            if (face == facing.rotateY()) {
                return MinecraftCornerState.OUTER_RIGHT;
            }
            if (face == facing.rotateYCCW()) {
                return MinecraftCornerState.OUTER_LEFT;
            }
        }

        Block front = getSide(facing.getOpposite());
        if (front.isStairs() && isUpsideDown(front) == upsideDown) {
            BlockFace face = getBlockFace(front);
            if (face == facing.rotateY()) {
                return MinecraftCornerState.INNER_RIGHT;
            }
            if (face == facing.rotateYCCW()) {
                return MinecraftCornerState.INNER_LEFT;
            }
        }

        return MinecraftCornerState.NONE;
    }
}
