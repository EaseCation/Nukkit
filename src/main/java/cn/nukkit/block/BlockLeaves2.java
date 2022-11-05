package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLeaves2 extends BlockLeaves {
    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public static final int TYPE_MASK = 0b1;
    public static final int UPDATE_BIT = 0b10;
    public static final int PERSISTENT_BIT = 0b100;

    private static final String[] NAMES = new String[]{
            "Acacia Leaves",
            "Dark Oak Leaves",
    };

    public BlockLeaves2() {
        this(0);
    }

    public BlockLeaves2(int meta) {
        super(meta & 0x7);
    }

    public String getName() {
        return NAMES[this.getLeafType()];
    }

    @Override
    public int getId() {
        return LEAVES2;
    }

    @Override
    protected boolean canDropApple() {
        return this.getLeafType() == DARK_OAK;
    }

    @Override
    protected Item getSapling() {
        return Item.get(BlockID.SAPLING, this.getLeafType() | 0b100);
    }

    @Override
    public int getLeafType() {
        return getDamage() & TYPE_MASK;
    }

    @Override
    public boolean isCheckDecay() {
        return (getDamage() & UPDATE_BIT) != 0;
    }

    @Override
    public void setCheckDecay(boolean checkDecay) {
        setDamage(checkDecay ? getDamage() | UPDATE_BIT : getDamage() & ~UPDATE_BIT);
    }

    @Override
    public boolean isPersistent() {
        return (getDamage() & PERSISTENT_BIT) != 0;
    }

    @Override
    public void setPersistent(boolean persistent) {
        setDamage(persistent ? getDamage() | PERSISTENT_BIT : getDamage() & ~PERSISTENT_BIT);
    }
}
