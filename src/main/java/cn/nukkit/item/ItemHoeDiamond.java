package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHoeDiamond extends ItemTool {

    public ItemHoeDiamond() {
        this(0, 1);
    }

    public ItemHoeDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeDiamond(Integer meta, int count) {
        super(DIAMOND_HOE, meta, count, "Diamond Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
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
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }
}
