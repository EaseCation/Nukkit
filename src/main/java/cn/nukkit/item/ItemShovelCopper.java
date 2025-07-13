package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemShovelCopper extends ItemTool {
    public ItemShovelCopper() {
        this(0, 1);
    }

    public ItemShovelCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelCopper(Integer meta, int count) {
        super(COPPER_SHOVEL, meta, count, "Copper Shovel");
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.SHOVEL;
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
        return 2;
    }
}
