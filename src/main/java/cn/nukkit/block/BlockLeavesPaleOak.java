package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockLeavesPaleOak extends BlockLeaves {
    public static final int TYPE_MASK = 0;
    public static final int UPDATE_BIT = 0b1;
    public static final int PERSISTENT_BIT = 0b10;

    public BlockLeavesPaleOak() {
        this(0);
    }

    public BlockLeavesPaleOak(int meta) {
        super(meta & 0x3);
    }

    @Override
    public int getId() {
        return PALE_OAK_LEAVES;
    }

    public String getName() {
        return "Pale Oak Leaves";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected boolean canDropApple() {
        return false;
    }

    @Override
    protected Item getSapling() {
        return Item.get(getItemId(PALE_OAK_SAPLING));
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
