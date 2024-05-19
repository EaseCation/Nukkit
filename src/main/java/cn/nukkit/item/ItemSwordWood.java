package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSwordWood extends ItemTool {

    public ItemSwordWood() {
        this(0, 1);
    }

    public ItemSwordWood(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordWood(Integer meta, int count) {
        super(WOODEN_SWORD, meta, count, "Wooden Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.SWORD;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
