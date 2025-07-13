package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemSwordCopper extends ItemTool {
    public ItemSwordCopper() {
        this(0, 1);
    }

    public ItemSwordCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordCopper(Integer meta, int count) {
        super(COPPER_SWORD, meta, count, "Copper Sword");
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.SWORD;
    }

    @Override
    public int getTier() {
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_COPPER;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }
}
