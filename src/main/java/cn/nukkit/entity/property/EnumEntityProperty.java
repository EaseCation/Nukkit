package cn.nukkit.entity.property;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ToString(callSuper = true)
public class EnumEntityProperty extends EntityProperty {
    private final List<String> values;
    private final int defaultValueIndex;

    public EnumEntityProperty(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    public EnumEntityProperty(String name, int defaultValueIndex, String... values) {
        this(name, defaultValueIndex, Arrays.asList(values));
    }

    public EnumEntityProperty(String name, Collection<String> values) {
        this(name, 0, values);
    }

    public EnumEntityProperty(String name, int defaultValueIndex, Collection<String> values) {
        super(name);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("empty values");
        }
        this.values = List.copyOf(values);
        this.defaultValueIndex = defaultValueIndex;
    }

    @Override
    public EntityPropertyType getType() {
        return EntityPropertyType.ENUM;
    }

    @Override
    public Number load(Tag tag) {
        if (!(tag instanceof StringTag stringTag)) {
            return null;
        }
        int enumIndex = values.indexOf(stringTag.data);
        if (enumIndex == -1) {
            return null;
        }
        return enumIndex;
    }

    @Override
    public void save(CompoundTag nbt, Number value) {
        nbt.putString(getName(), value != null ? values.get(value.intValue()) : getDefaultValue());
    }

    @Override
    public CompoundTag toNBT() {
        ListTag<StringTag> enumTag = new ListTag<>("enum");
        for (String value : values) {
            enumTag.addString(value);
        }
        return new CompoundTag()
                .putString("name", getName())
                .putList("enum", enumTag)
                .putInt("type", getType().ordinal());
    }

    @Override
    public Object getDefaultData() {
        return getDefaultValue();
    }

    @Override
    public Number getMinimumNumber() {
        return getMinimumIntValue();
    }

    @Override
    public Number getMaximumNumber() {
        return getMaximumIntValue();
    }

    @Override
    public Number getDefaultNumber() {
        return getDefaultIntValue();
    }

    @Override
    public int getMinimumIntValue() {
        return 0;
    }

    @Override
    public int getMaximumIntValue() {
        return values.size() - 1;
    }

    @Override
    public int getDefaultIntValue() {
        return defaultValueIndex;
    }

    public String getDefaultValue() {
        return values.get(defaultValueIndex);
    }

    public List<String> getValues() {
        return values;
    }
}
