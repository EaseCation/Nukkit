package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * http://minecraft.gamepedia.com/End_Rod
 *
 * @author PikyCZ
 */
public class BlockEndRod extends BlockTransparent implements Faceable {

    BlockEndRod() {

    }

    @Override
    public String getName() {
        return "End Rod";
    }

    @Override
    public int getId() {
        return END_ROD;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getBlockFace().getAxis()) {
            default:
            case Y:
                return new SimpleAxisAlignedBB(this.x + 6.0 / 16, this.y, this.z + 6.0 / 16, this.x + 1 - 6.0 / 16, this.y + 1, this.z + 1 - 6.0 / 16);
            case X:
                return new SimpleAxisAlignedBB(this.x, this.y + 6.0 / 16, this.z + 6.0 / 16, this.x + 1, this.y + 1 - 6.0 / 16, this.z + 1 - 6.0 / 16);
            case Z:
                return new SimpleAxisAlignedBB(this.x + 6.0 / 16, this.y + 6.0 / 16, this.z, this.x + 1 - 6.0 / 16, this.y + 1 - 6.0 / 16, this.z + 1);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockFace facing = face.isVertical() ? face : face.getOpposite();

        if (getSide(face.getOpposite()).is(END_ROD, facing.getIndex())) {
            facing = facing.getOpposite();
        }

        this.setDamage(facing.getIndex());

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        BlockFace facing = BlockFace.fromIndex(this.getDamage() & 0x07);
        if (facing.isHorizontal()) {
            facing = facing.getOpposite();
        }
        return facing;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return getBlockFace().getAxis() == face.getAxis() && type == SupportType.CENTER;
    }
}
