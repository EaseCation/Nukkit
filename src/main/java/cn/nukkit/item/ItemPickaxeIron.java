package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPickaxeIron extends ItemTool {

    public ItemPickaxeIron() {
        this(0, 1);
    }

    public ItemPickaxeIron(Integer meta) {
        this(meta, 1);
    }

    public ItemPickaxeIron(Integer meta, int count) {
        super(IRON_PICKAXE, meta, count, "Iron Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
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
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}
