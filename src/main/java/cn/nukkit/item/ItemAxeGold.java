package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAxeGold extends ItemTool {

    public ItemAxeGold() {
        this(0, 1);
    }

    public ItemAxeGold(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeGold(Integer meta, int count) {
        super(GOLDEN_AXE, meta, count, "Gold Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }
}
