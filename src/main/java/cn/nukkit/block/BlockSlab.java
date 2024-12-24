package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparent {

    public static final int TYPE_MASK = 0b111;
    public static final int TOP_SLOT_BIT = 0b1000;

    protected BlockSlab(int meta) {
        super(meta);
    }

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
        setDamage(getSlabType());

        if (face == BlockFace.DOWN) {
            if (isSameSlabType(target) && ((BlockSlab) target).isTopSlot()) {
                level.setExtraBlock(target, Blocks.air(), true, false);
                level.setBlock(target, get(getDoubleSlabBlockId(), getSlabType()), true);
                return true;
            }

            if (isSameSlabType(block)) {
                level.setExtraBlock(block, Blocks.air(), true, false);
                level.setBlock(block, get(getDoubleSlabBlockId(), getSlabType()), true);
                return true;
            }

            if (!block.canBeReplaced()) {
                return false;
            }

            setDamage(getDamage() | getTopSlotBit());
            level.setBlock(block, this, true);
            return true;
        }
        if (face == BlockFace.UP) {
            if (isSameSlabType(target) && !((BlockSlab) target).isTopSlot()) {
                level.setExtraBlock(target, Blocks.air(), true, false);
                level.setBlock(target, get(getDoubleSlabBlockId(), getSlabType()), true);
                return true;
            }

            if (isSameSlabType(block)) {
                level.setExtraBlock(block, Blocks.air(), true, false);
                level.setBlock(block, get(getDoubleSlabBlockId(), getSlabType()), true);
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
            level.setBlock(block, get(getDoubleSlabBlockId(), getSlabType()), true);
            return true;
        }

        if (!block.canBeReplaced()) {
            return false;
        }

        if (topSlot) {
            setDamage(getDamage() | getTopSlotBit());
        }

        level.setBlock(block, this, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getSlabType());
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

    public int getSlabType() {
        return getDamage() & TYPE_MASK;
    }

    public boolean isTopSlot() {
        return (getDamage() & getTopSlotBit()) != 0;
    }

    private boolean isSameSlabType(Block block) {
        return getId() == block.getId() && getSlabType() == ((BlockSlab) block).getSlabType();
    }

    protected int getTopSlotBit() {
        return TOP_SLOT_BIT;
    }

    protected abstract int getDoubleSlabBlockId();
}