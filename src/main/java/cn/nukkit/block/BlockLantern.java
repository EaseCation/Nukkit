package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockLantern extends BlockTransparentMeta {

    public static final int HANGING_BIT = 0b1;

    public BlockLantern() {
        this(0);
    }

    public BlockLantern(int meta) {
        super(meta);
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
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 25;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE));
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

    public boolean isHanging() {
        return (getDamage() & HANGING_BIT) == HANGING_BIT;
    }
}
