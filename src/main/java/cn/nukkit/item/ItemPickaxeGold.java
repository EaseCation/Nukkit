package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPickaxeGold extends ItemTool {

    public ItemPickaxeGold() {
        this(0, 1);
    }

    public ItemPickaxeGold(Integer meta) {
        this(meta, 1);
    }

    public ItemPickaxeGold(Integer meta, int count) {
        super(GOLDEN_PICKAXE, meta, count, "Gold Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
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
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}
