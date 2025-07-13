package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemHoeCopper extends ItemTool {
    public ItemHoeCopper() {
        this(0, 1);
    }

    public ItemHoeCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeCopper(Integer meta, int count) {
        super(COPPER_HOE, meta, count, "Copper Hoe");
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
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_COPPER;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }
}
