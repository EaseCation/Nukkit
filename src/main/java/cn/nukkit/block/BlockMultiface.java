package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

import static cn.nukkit.GameVersion.*;

public abstract class BlockMultiface extends BlockTransparentMeta {
    protected BlockMultiface(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (getId() == block.getId()) {
            setDamage(block.getDamage());
        } else {
            setDamage(0);
        }

        if (canBeSupportedBy(target, face) && addFace(face.getOpposite())) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }

        boolean place = false;

        for (BlockFace side : BlockFace.getValues()) {
            if (hasFace(side)) {
                continue;
            }

            if (!canBeSupportedBy(getSide(side), side.getOpposite())) {
                continue;
            }

            addFace(side);
            place = true;
            break;
        }

        if (!place) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            boolean update = false;
            for (BlockFace face : BlockFace.getValues()) {
                if (!hasFace(face)) {
                    continue;
                }

                if (canBeSupportedBy(getSide(face), face.getOpposite())) {
                    continue;
                }

                removeFace(face);
                update = true;
            }

            if (!update) {
                return 0;
            }

            if (getDamage() != 0) {
                level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
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

    protected boolean canBeSupportedBy(Block block, BlockFace side) {
        return block.isLeaves() || SupportType.hasFullSupport(block, side);
    }

    protected boolean hasFace(BlockFace face) {
        if (!V1_18_10.isAvailable() || face.isVertical()) {
            return (getDamage() & (1 << face.getIndex())) != 0;
        }

        return (getDamage() & (1 << (face.getHorizontalIndex() + 2))) != 0;
    }

    protected boolean addFace(BlockFace face) {
        int meta = getDamage();
        int old = meta;

        if (!V1_18_10.isAvailable() || face.isVertical()) {
            meta |= 1 << face.getIndex();
        } else {
            meta |= 1 << (face.getHorizontalIndex() + 2);
        }

        if (meta == old) {
            return false;
        }

        setDamage(meta);
        return true;
    }

    protected boolean removeFace(BlockFace face) {
        int meta = getDamage();
        int old = meta;

        if (!V1_18_10.isAvailable() || face.isVertical()) {
            meta &= ~(1 << face.getIndex());
        } else {
            meta &= ~(1 << (face.getHorizontalIndex() + 2));
        }

        if (meta == old) {
            return false;
        }

        setDamage(meta);
        return true;
    }
}
