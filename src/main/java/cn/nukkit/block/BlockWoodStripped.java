package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

public abstract class BlockWoodStripped extends BlockSolidMeta {

    public static final int PILLAR_Y_AXIS = 0;
    public static final int PILLAR_X_AXIS = 1;
    public static final int PILLAR_Z_AXIS = 2;

    protected BlockWoodStripped() {
        this(0);
    }

    protected BlockWoodStripped(int meta) {
        super(meta);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int meta;
        switch (face.getAxis()) {
            default:
            case Y:
                meta = PILLAR_Y_AXIS;
                break;
            case X:
                meta = PILLAR_X_AXIS;
                break;
            case Z:
                meta = PILLAR_Z_AXIS;
                break;
        }
        setDamage(meta);
        return level.setBlock(block, this, true);
    }

    @Override
    public boolean isLog() {
        return true;
    }
}
