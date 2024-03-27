package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemShovelGold extends ItemTool {

    public ItemShovelGold() {
        this(0, 1);
    }

    public ItemShovelGold(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelGold(Integer meta, int count) {
        super(GOLDEN_SHOVEL, meta, count, "Gold Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
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
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }
}
