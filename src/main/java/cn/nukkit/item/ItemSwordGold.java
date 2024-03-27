package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSwordGold extends ItemTool {

    public ItemSwordGold() {
        this(0, 1);
    }

    public ItemSwordGold(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordGold(Integer meta, int count) {
        super(GOLDEN_SWORD, meta, count, "Gold Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
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
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }
}
