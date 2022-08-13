package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemShears extends ItemTool {

    public ItemShears() {
        this(0, 1);
    }

    public ItemShears(Integer meta) {
        this(meta, 1);
    }

    public ItemShears(Integer meta, int count) {
        super(SHEARS, meta, count, "Shears");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_SHEARS;
    }

    @Override
    public boolean isShears() {
        return true;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }
}
