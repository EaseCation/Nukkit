package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import javax.annotation.Nullable;

public class ItemBundle extends Item {
    public ItemBundle() {
        this( 0, 1);
    }

    public ItemBundle(Integer meta) {
        this(meta, 1);
    }

    public ItemBundle(Integer meta, int count) {
        super(BUNDLE, meta, count, "Bundle");
    }

    protected ItemBundle(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public ItemBundle setBundleWeight(int weight) {
        setNamedTag(getOrCreateNamedTag().putInt("bundle_weight", weight));
        return this;
    }

    public int getBundleWeight() {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return 4;
        }
        return tag.getInt("bundle_weight", 4);
    }

    @Nullable
    public ListTag<CompoundTag> getBundleContents() {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return null;
        }
        return tag.getList("storage_item_component_content", (ListTag<CompoundTag>) null);
    }

    public ItemBundle setDynamicId(int id) {
        setNamedTag(new CompoundTag().putInt("bundle_id", id));
        return this;
    }

    public int getDynamicId() {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return -1;
        }
        return tag.getInt("bundle_id", -1);
    }

    @Override
    public boolean isBundle() {
        return true;
    }
}
