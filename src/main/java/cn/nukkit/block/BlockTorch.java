package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockTorch extends BlockFlowable implements Faceable {

    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int FACING_DIRECTION_BITS = 3;

    private static final BlockFace[] CHECK_SIDE = {
            BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.DOWN, // ordered
    };

    public BlockTorch() {
        this(0);
    }

    public BlockTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Torch";
    }

    @Override
    public int getId() {
        return TORCH;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = this.getBlockFace();
            if (!canBeSupportedBy(getSide(face.getOpposite()), face)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        if (block.isLava() || canBeFlowedInto() && (block.isWater() || !block.isAir() && level.getExtraBlock(this).isWater())) {
            return false;
        }

        BlockFace facing = null;
        if (canBeSupportedBy(target, face)) {
            facing = face;
        } else {
            for (BlockFace side : CHECK_SIDE) {
                if (side == face) {
                    continue;
                }

                Block sideBlock = getSide(side);
                if (!canBeSupportedBy(sideBlock, side.getOpposite())) {
                    continue;
                }

                facing = side.getOpposite();
                break;
            }

            if (facing == null) {
                return false;
            }
        }

        setBlockFace(facing);
        this.level.setBlock(block, this, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        int meta = this.getDamage() & FACING_DIRECTION_MASK;
        return meta != 0 ? BlockFace.fromIndex(6 - meta) : BlockFace.UP;
    }

    protected void setBlockFace(BlockFace face) {
        setDamage(6 - face.getIndex());
    }

    protected boolean canBeSupportedBy(Block support, BlockFace face) {
        if (support.getId() == MOB_SPAWNER) {
            return face != BlockFace.DOWN;
        }

        return face == BlockFace.UP && SupportType.hasCenterSupport(support, BlockFace.UP)
                || face != BlockFace.DOWN && SupportType.hasFullSupport(support, face);
    }
}
