package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHoeIron extends ItemTool {

    public ItemHoeIron() {
        this(0, 1);
    }

    public ItemHoeIron(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeIron(Integer meta, int count) {
        super(IRON_HOE, meta, count, "Iron Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
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
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}
