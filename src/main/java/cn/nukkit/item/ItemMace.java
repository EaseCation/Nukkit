package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemMace extends ItemTool {
    public ItemMace() {
        this(0, 1);
    }

    public ItemMace(Integer meta) {
        this(meta, 1);
    }

    public ItemMace(Integer meta, int count) {
        super(MACE, meta, count, "Mace");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_MACE;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.SWORD;
    }

    @Override
    public boolean isSword() {
        return true;
    }
}
