package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemAxeCopper extends ItemTool {
    public ItemAxeCopper() {
        this(0, 1);
    }

    public ItemAxeCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeCopper(Integer meta, int count) {
        super(COPPER_AXE, meta, count, "Copper Axe");
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
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_COPPER;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}
