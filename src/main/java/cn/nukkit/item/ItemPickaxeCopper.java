package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemPickaxeCopper extends ItemTool {
    public ItemPickaxeCopper() {
        this(0, 1);
    }

    public ItemPickaxeCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemPickaxeCopper(Integer meta, int count) {
        super(COPPER_PICKAXE, meta, count, "Copper Pickaxe");
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.PICKAXE;
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
