package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLadder extends BlockTransparent implements Faceable {

    private static final int[] FACES = {
            0, //never use
            1, //never use
            3,
            2,
            5,
            4
    };

    public BlockLadder() {
        this(0);
    }

    public BlockLadder(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Ladder";
    }

    @Override
    public int getId() {
        return LADDER;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public float getHardness() {
        return 0.4f;
    }

    @Override
    public float getResistance() {
        return 2;
    }

    @Override
    public double getMinX() {
        return this.x + (getDamage() == 4 ? 1 - 0.1875f : 0);
    }

    @Override
    public double getMinZ() {
        return this.z + (getDamage() == 2 ? 1 - 0.1875f : 0);
    }

    @Override
    public double getMaxX() {
        return this.x + (getDamage() == 5 ? 0.1875f : 1);
    }

    @Override
    public double getMaxZ() {
        return this.z + (getDamage() == 3 ? 0.1875f : 1);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (getBlockFace().isVertical()) {
            return null;
        }
        return this;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public Block getPlacementBlock(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (target.isTransparent() || face.isVertical()) {
            return this;
        }
        return get(getId(), face.getIndex());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!target.isTransparent()) {
            if (face.isHorizontal()) {
                this.setDamage(face.getIndex());
                this.getLevel().setBlock(block, this, true, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.getSide(BlockFace.fromIndex(FACES[this.getDamage()])).isSolid()) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
