package cn.nukkit.item;

public class ItemBoneMeal extends Item {
    public ItemBoneMeal() {
        this(0, 1);
    }

    public ItemBoneMeal(Integer meta) {
        this(meta, 1);
    }

    public ItemBoneMeal(Integer meta, int count) {
        super(BONE_MEAL, meta, count, "Bone Meal");
    }

    @Override
    public boolean isFertilizer() {
        return true;
    }
}
