package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparent {

    @Deprecated
    public static final int TYPE_MASK = 0b111;
    public static final int TOP_SLOT_BIT = 0b1000;

    @Override
    public double getMinY() {
        return isTopSlot() ? this.y + 0.5 : this.y;
    }

    @Override
    public double getMaxY() {
        return isTopSlot() ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face == BlockFace.DOWN) {
            if (isSameSlabType(target) && ((BlockSlab) target).isTopSlot()) {
                level.setExtraBlock(target, Blocks.air(), true, false);
                level.setBlock(target, get(getDoubleSlabBlockId()), true);
                return true;
            }

            if (isSameSlabType(block)) {
                level.setExtraBlock(block, Blocks.air(), true, false);
                level.setBlock(block, get(getDoubleSlabBlockId()), true);
                return true;
            }

            if (!block.canBeReplaced()) {
                return false;
            }

            setTopSlot(true);
            level.setBlock(block, this, true);
            return true;
        }
        if (face == BlockFace.UP) {
            if (isSameSlabType(target) && !((BlockSlab) target).isTopSlot()) {
                level.setExtraBlock(target, Blocks.air(), true, false);
                level.setBlock(target, get(getDoubleSlabBlockId()), true);
                return true;
            }

            if (isSameSlabType(block)) {
                level.setExtraBlock(block, Blocks.air(), true, false);
                level.setBlock(block, get(getDoubleSlabBlockId()), true);
                return true;
            }

            if (!block.canBeReplaced()) {
                return false;
            }

            level.setBlock(block, this, true);
            return true;
            //TODO: check for collision
        }

        boolean topSlot = fy > 0.5;

        if (isSameSlabType(block) && ((BlockSlab) block).isTopSlot() != topSlot) {
            level.setExtraBlock(block, Blocks.air(), true, false);
            level.setBlock(block, get(getDoubleSlabBlockId()), true);
            return true;
        }

        if (!block.canBeReplaced()) {
            return false;
        }

        setTopSlot(topSlot);
        level.setBlock(block, this, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
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
                return isTopSlot();
            case DOWN:
                return !isTopSlot();
        }
        return false;
    }

    @Override
    public boolean isSlab() {
        return true;
    }

    @Override
    public boolean isHalfSlab() {
        return true;
    }

    @Deprecated
    public int getSlabType() {
        return 0;
    }

    public boolean isTopSlot() {
        return getDamage() == TOP_SLOT_BIT;
    }

    public void setTopSlot(boolean upper) {
        setDamage(upper ? TOP_SLOT_BIT : 0);
    }

    private boolean isSameSlabType(Block block) {
        return getId() == block.getId();
    }

    protected abstract int getDoubleSlabBlockId();
}