package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemElytra extends ItemArmor {

    public ItemElytra() {
        this(0, 1);
    }

    public ItemElytra(Integer meta) {
        this(meta, 1);
    }

    public ItemElytra(Integer meta, int count) {
        super(ELYTRA, meta, count, "Elytra");
    }

    @Override
    public int getMaxDurability() {
        return 432;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getDamageChance(int unbreaking) {
        return 100 / (unbreaking + 1);
    }

    @Override
    public ItemElytra setTrim(String pattern, String material) {
        return this;
    }

    @Override
    public boolean clearTrim() {
        return false;
    }
}
