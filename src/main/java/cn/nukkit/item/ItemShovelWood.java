package cn.nukkit.item;

import cn.nukkit.block.BlockToolType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemShovelWood extends ItemTool {

    public ItemShovelWood() {
        this(0, 1);
    }

    public ItemShovelWood(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelWood(Integer meta, int count) {
        super(WOODEN_SHOVEL, meta, count, "Wooden Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
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
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
