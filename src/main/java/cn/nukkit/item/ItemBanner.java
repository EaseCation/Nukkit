package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;

/**
 * Created by PetteriM1
 */
public class ItemBanner extends Item {

    public ItemBanner() {
        this(0);
    }

    public ItemBanner(Integer meta) {
        this(meta, 1);
    }

    public ItemBanner(Integer meta, int count) {
        super(BANNER, meta, count, DyeColor.getByWoolData(meta != null ? meta : 0).getName() + " Banner");
        this.block = Block.get(Block.STANDING_BANNER);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    public int getBaseColor() {
        return this.getDamage();
    }

    public void setBaseColor(DyeColor color) {
        this.setDamage(color.getDyeData());
    }

    public int getType() {
        CompoundTag tag = this.getNamedTag();
        if (tag == null) {
            return BlockEntityBanner.TYPE_DEFAULT;
        }
        return tag.getInt("Type");
    }

    public void setType(int type) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        tag.putInt("Type", type);
        this.setNamedTag(tag);
    }

    public boolean isDefaultBanner() {
        return getType() == BlockEntityBanner.TYPE_DEFAULT;
    }

    public void addPattern(BannerPattern pattern) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag("").
                putInt("Color", pattern.getColor().getDyeData() & 0x0f).
                putString("Pattern", pattern.getType().getName()));
        tag.putList(patterns);
        this.setNamedTag(tag);
    }

    public BannerPattern getPattern(int index) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        return BannerPattern.fromCompoundTag(tag.getList("Patterns").size() > index && index >= 0 ? tag.getList("Patterns", CompoundTag.class).get(index) : new CompoundTag());
    }

    public void removePattern(int index) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
            if (patterns.isEmpty()) {
                tag.remove("Patterns");
            }
        }
        this.setNamedTag(tag);
    }

    public boolean removeLastPattern() {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return false;
        }

        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        if (patterns.isEmpty()) {
            return false;
        }

        patterns.remove(patterns.size() - 1);
        if (patterns.isEmpty()) {
            tag.remove("Patterns");
        }

        setNamedTag(tag);
        return true;
    }

    public int getPatternsSize() {
        return (this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).getList("Patterns").size();
    }

    public DyeColor getColor() {
        return DyeColor.getByDyeData(this.getDamage());
    }
}
