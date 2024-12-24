package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nullable;

public class ItemShield extends ItemTool {

    public ItemShield() {
        this(0, 1);
    }

    public ItemShield(Integer meta) {
        this(meta, 1);
    }

    public ItemShield(Integer meta, int count) {
        super(SHIELD, meta, count, "Shield");
    }

    @Override
    public boolean canDualWield() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_SHIELD;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }

    @Override
    public int getEquippingSound() {
        return LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
    }

    public ItemShield setBaseColor(DyeColor color) {
        return setBaseColor(color.getDyeData());
    }

    public ItemShield setBaseColor(int dyeColor) {
        setNamedTag(getOrCreateNamedTag().putInt("Base", dyeColor & 0xf));
        return this;
    }

    public int getBaseColor() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return -1;
        }
        return nbt.getInt("Base", -1);
    }

    public ItemShield addPattern(BannerPattern pattern) {
        CompoundTag nbt = getOrCreateNamedTag();
        setNamedTag(nbt.putList("Patterns", nbt.getList("Patterns", CompoundTag.class).add(new CompoundTag()
                .putInt("Color", pattern.getColor().getDyeData() & 0xf)
                .putString("Pattern", pattern.getType().getName()))));
        return this;
    }

    @Nullable
    public BannerPattern getPattern(int index) {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return null;
        }

        ListTag<CompoundTag> patterns = nbt.getList("Patterns", CompoundTag.class);
        if (index <= patterns.size()) {
            return null;
        }

        return BannerPattern.fromCompoundTag(patterns.get(index));
    }

    public boolean removePattern(int index) {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return false;
        }

        ListTag<CompoundTag> patterns = nbt.getList("Patterns", CompoundTag.class);
        if (index <= patterns.size()) {
            return false;
        }

        patterns.remove(index);

        if (patterns.isEmpty()) {
            nbt.remove("Patterns");
        }

        setNamedTag(nbt);
        return true;
    }

    public boolean removeLastPattern() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return false;
        }

        ListTag<CompoundTag> patterns = nbt.getList("Patterns", CompoundTag.class);
        if (patterns.isEmpty()) {
            return false;
        }

        patterns.remove(patterns.size() - 1);

        if (patterns.isEmpty()) {
            nbt.remove("Patterns");
        }

        setNamedTag(nbt);
        return true;
    }

    public int getPatternsSize() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return 0;
        }
        return nbt.getList("Patterns").size();
    }
}
