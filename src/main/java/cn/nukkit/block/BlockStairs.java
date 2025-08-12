package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockStairs extends BlockTransparent implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int UPSIDE_DOWN_BIT = 0b100;

    protected BlockStairs(int meta) {
        super(meta & 0b111);
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
    public boolean collidesWithBB(AxisAlignedBB bb) {
        int damage = this.getDamage();
        int side = damage & DIRECTION_MASK;
        double f = 0;
        double f1 = 0.5;
        double f2 = 0.5;
        double f3 = 1;
        if ((damage & UPSIDE_DOWN_BIT) == UPSIDE_DOWN_BIT) {
            f = 0.5;
            f1 = 1;
            f2 = 0;
            f3 = 0.5;
        }

        if (bb.intersectsWith(new SimpleAxisAlignedBB(
                this.x,
                this.y + f,
                this.z,
                this.x + 1,
                this.y + f1,
                this.z + 1
        ))) {
            return true;
        }

        if (side == 0) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x + 0.5,
                    this.y + f2,
                    this.z,
                    this.x + 1,
                    this.y + f3,
                    this.z + 1
            ))) {
                return true;
            }
        } else if (side == 1) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x,
                    this.y + f2,
                    this.z,
                    this.x + 0.5,
                    this.y + f3,
                    this.z + 1
            ))) {
                return true;
            }
        } else if (side == 2) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x,
                    this.y + f2,
                    this.z + 0.5,
                    this.x + 1,
                    this.y + f3,
                    this.z + 1
            ))) {
                return true;
            }
        } else if (side == 3) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x,
                    this.y + f2,
                    this.z,
                    this.x + 1,
                    this.y + f3,
                    this.z + 0.5
            ))) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromReversedHorizontalIndex(this.getDamage() & DIRECTION_MASK);
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
        return (getDamage() & UPSIDE_DOWN_BIT) == UPSIDE_DOWN_BIT;
    }

    @Override
    public boolean isStairs() {
        return true;
    }
}
