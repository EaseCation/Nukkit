package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockLeavesMangrove extends BlockLeaves {
    public static final int TYPE_MASK = 0;
    public static final int UPDATE_BIT = 0b1;
    public static final int PERSISTENT_BIT = 0b10;

    public BlockLeavesMangrove() {
        this(0);
    }

    public BlockLeavesMangrove(int meta) {
        super(meta & 0x3);
    }

    @Override
    public int getId() {
        return MANGROVE_LEAVES;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    public String getName() {
        return "Mangrove Leaves";
    }

    @Override
    protected boolean canDropApple() {
        return false;
    }

    @Override
    protected boolean canDropSapling() {
        return false;
    }

    @Override
    protected Item getSapling() {
        return Item.get(getItemId(MANGROVE_PROPAGULE));
    }

    @Override
    public int getLeafType() {
        return 0;
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
