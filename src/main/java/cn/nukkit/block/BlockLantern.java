package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockLantern extends BlockTransparent {

    public static final int HANGING_BIT = 0b1;

    BlockLantern() {

    }

    @Override
    public int getId() {
        return LANTERN;
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 3.5f;
        }
        return 5;
    }

    @Override
    public float getResistance() {
        return 17.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Block getPlacementBlock(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        int meta;
        if (face.isVertical()) {
            if (SupportType.hasCenterSupport(getSide(face.getOpposite()), face)) {
                meta = face.getOpposite().getIndex();
            } else if (SupportType.hasCenterSupport(getSide(face), face.getOpposite())) {
                meta = face.getIndex();
            } else {
                return this;
            }
        } else if (SupportType.hasCenterSupport(down(), BlockFace.UP)) {
            meta = 0;
        } else if (SupportType.hasCenterSupport(up(), BlockFace.DOWN)) {
            meta = HANGING_BIT;
        } else {
            return this;
        }
        return get(getId(), meta);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face.isVertical()) {
            if (SupportType.hasCenterSupport(getSide(face.getOpposite()), face)) {
                setDamage(face.getOpposite().getIndex());
            } else if (SupportType.hasCenterSupport(getSide(face), face.getOpposite())) {
                setDamage(face.getIndex());
            } else {
                return false;
            }
        } else if (SupportType.hasCenterSupport(down(), BlockFace.UP)) {
            setDamage(0);
        } else if (SupportType.hasCenterSupport(up(), BlockFace.DOWN)) {
            setDamage(HANGING_BIT);
        } else {
            return false;
        }
        return level.setBlock(block, this, true);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = isHanging() ? BlockFace.UP : BlockFace.DOWN;
            if (!SupportType.hasCenterSupport(getSide(face), face.getOpposite())) {
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public double getMinX() {
        return x + 5 / 16.0;
    }

    @Override
    public double getMinY() {
        return y + (isHanging() ? 2 / 16.0 : 0);
    }

    @Override
    public double getMinZ() {
        return z + 5 / 16.0;
    }

    @Override
    public double getMaxX() {
        return x + 1 - 5 / 16.0;
    }

    @Override
    public double getMaxY() {
        return y + 1 - (isHanging() ? 6 / 16.0 : 8 / 16.0);
    }

    @Override
    public double getMaxZ() {
        return z + 1 - 5 / 16.0;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.METAL_BLOCK_COLOR;
    }

    @Override
    public boolean isLantern() {
        return true;
    }

    public boolean isHanging() {
        return (getDamage() & HANGING_BIT) == HANGING_BIT;
    }
}
