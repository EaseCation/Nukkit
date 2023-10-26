package cn.nukkit.item;

public class ItemBrush extends ItemTool {
    public ItemBrush() {
        this(0, 1);
    }

    public ItemBrush(Integer meta) {
        this(meta, 1);
    }

    public ItemBrush(Integer meta, int count) {
        super(BRUSH, meta, count, "Brush");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BRUSH;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }
}
