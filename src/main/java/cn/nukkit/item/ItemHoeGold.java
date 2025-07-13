package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHoeGold extends ItemTool {

    public ItemHoeGold() {
        this(0, 1);
    }

    public ItemHoeGold(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeGold(Integer meta, int count) {
        super(GOLDEN_HOE, meta, count, "Gold Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}
