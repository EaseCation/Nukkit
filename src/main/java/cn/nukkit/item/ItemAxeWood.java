package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAxeWood extends ItemTool {

    public ItemAxeWood() {
        this(0, 1);
    }

    public ItemAxeWood(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeWood(Integer meta, int count) {
        super(WOODEN_AXE, meta, count, "Wooden Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
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
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
