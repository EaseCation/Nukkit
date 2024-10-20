package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nullable;
import java.awt.Color;

public class ItemWolfArmor extends Item implements ItemDurable {
    public ItemWolfArmor() {
        this(0, 1);
    }

    public ItemWolfArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemWolfArmor(Integer meta, int count) {
        super(WOLF_ARMOR, meta, count, "Wolf Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 64;
    }

    @Override
    public int getArmorPoints() {
        return 11;
    }

    public void setColor(DyeColor dye) {
        setColor(dye.getColor());
    }

    public void setColor(Color color) {
        setColor(color.getRGB());
    }

    public void setColor(int r, int g, int b) {
        setColor(r << 16 | g << 8 | b);
    }

    public void setColor(int rgb) {
        CompoundTag nbt = getOrCreateNamedTag();
        nbt.putInt("customColor", 0xff000000 | rgb);
        setNamedTag(nbt);
    }

    @Nullable
    public BlockColor getColor() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return null;
        }
        if (!nbt.exist("customColor")) {
            return null;
        }
        return new BlockColor(nbt.getInt("customColor"));
    }

    public boolean clearColor() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return false;
        }
        if (nbt.remove("customColor") == null) {
            return false;
        }
        setNamedTag(nbt);
        return true;
    }
}
