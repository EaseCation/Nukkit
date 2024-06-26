package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

public class ItemShovelNetherite extends ItemTool {

    public ItemShovelNetherite() {
        this(0, 1);
    }

    public ItemShovelNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelNetherite(Integer meta, int count) {
        super(NETHERITE_SHOVEL, meta, count, "Netherite Shovel");
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
    public int getAttackDamage() {
        return 5;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }
}
