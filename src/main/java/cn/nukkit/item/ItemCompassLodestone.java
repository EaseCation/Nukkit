package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;

public class ItemCompassLodestone extends Item {

    public ItemCompassLodestone() {
        this(0, 1);
    }

    public ItemCompassLodestone(Integer meta) {
        this(meta, 1);
    }

    public ItemCompassLodestone(Integer meta, int count) {
        super(LODESTONE_COMPASS, meta, count, "Lodestone Compass");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public ItemCompassLodestone setTrackingLodestoneId(int id) {
        setNamedTag(getOrCreateNamedTag().putInt("trackingHandle", id));
        return this;
    }

    public int getTrackingLodestoneId() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return 0;
        }
        return nbt.getInt("trackingHandle");
    }
}
