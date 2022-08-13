package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparentMeta {

    public static final int TYPE_MASK = 0b111;
    public static final int TOP_SLOT_BIT = 0b1000;

    protected final int doubleSlab;

    public BlockSlab(int meta, int doubleSlab) {
        super(meta);
        this.doubleSlab = doubleSlab;
    }

    @Override
    public double getMinY() {
        return ((this.getDamage() & TOP_SLOT_BIT) == TOP_SLOT_BIT) ? this.y + 0.5 : this.y;
    }

    @Override
    public double getMaxY() {
        return ((this.getDamage() & TOP_SLOT_BIT) == TOP_SLOT_BIT) ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(this.getDamage() & TYPE_MASK);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab && (target.getDamage() & TOP_SLOT_BIT) == TOP_SLOT_BIT && (target.getDamage() & TYPE_MASK) == (this.getDamage() & TYPE_MASK)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getDamage() & TYPE_MASK) == (this.getDamage() & TYPE_MASK)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            } else {
                this.setDamage(this.getDamage() | TOP_SLOT_BIT);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab && (target.getDamage() & TOP_SLOT_BIT) == 0 && (target.getDamage() & TYPE_MASK) == (this.getDamage() & TYPE_MASK)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getDamage() & TYPE_MASK) == (this.getDamage() & TYPE_MASK)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab, this.getDamage()), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if ((block.getDamage() & TYPE_MASK) == (this.getDamage() & TYPE_MASK)) {
                    this.getLevel().setBlock(block, Block.get(doubleSlab, this.getDamage()), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    this.setDamage(this.getDamage() | TOP_SLOT_BIT);
                }
            }
        }

        if (block instanceof BlockSlab && (target.getDamage() & TYPE_MASK) != (this.getDamage() & TYPE_MASK)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDamage() & TYPE_MASK);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        switch (face) {
            case UP:
                return (getDamage() & TOP_SLOT_BIT) == TOP_SLOT_BIT;
            case DOWN:
                return (getDamage() & TOP_SLOT_BIT) == 0;
        }
        return false;
    }

    @Override
    public boolean isSlab() {
        return true;
    }

    public int getType() {
        return getDamage() & TYPE_MASK;
    }

    public boolean isTopSlot() {
        return (getDamage() & TOP_SLOT_BIT) == TOP_SLOT_BIT;
    }
}