package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHoeStone extends ItemTool {

    public ItemHoeStone() {
        this(0, 1);
    }

    public ItemHoeStone(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeStone(Integer meta, int count) {
        super(STONE_HOE, meta, count, "Stone Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getBlockToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }
}
