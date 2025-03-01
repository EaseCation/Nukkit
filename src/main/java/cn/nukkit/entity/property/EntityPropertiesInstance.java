package cn.nukkit.entity.property;

import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.*;

import javax.annotation.Nullable;
import java.util.Map.Entry;

public class EntityPropertiesInstance {
    private final EntityProperties properties;
    private final Number[] values;

    public EntityPropertiesInstance(EntityProperties properties) {
        this.properties = properties;
        values = new Number[properties.size()];
    }

    public void load(CompoundTag nbt) {
        if (properties.isEmpty()) {
            return;
        }

        for (Entry<String, Tag> entry : nbt.entrySet()) {
            EntityProperty property = getProperty(entry.getKey());
            if (property == null) {
                continue;
            }
            Number value = property.load(entry.getValue());
            if (value == null) {
                continue;
            }
            values[property.getIndex()] = value;
        }
    }

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        for (EntityProperty property : properties) {
            property.save(nbt, values[property.getIndex()]);
        }
        return nbt;
    }

    @Nullable
    public Pair<Int2IntMap, Int2FloatMap> getValues() {
        if (properties.isEmpty()) {
            return null;
        }

        Int2IntMap intProperties = new Int2IntOpenHashMap();
        Int2FloatMap floatProperties = new Int2FloatOpenHashMap();
        for (EntityProperty property : properties) {
            int index = property.getIndex();
            if (property.getType() != EntityPropertyType.FLOAT) {
                int value;
                Number number = values[index];
                if (number != null) {
                    value = number.intValue();
                } else {
                    value = property.getDefaultIntValue();
                }
                intProperties.put(index, value);
            } else {
                float value;
                Number number = values[index];
                if (number != null) {
                    value = number.floatValue();
                } else {
                    value = property.getDefaultFloatValue();
                }
                floatProperties.put(index, value);
            }
        }
        return Pair.of(intProperties, floatProperties);
    }

    @Nullable
    public EntityProperty getProperty(int index) {
        return properties.get(index);
    }

    @Nullable
    public EntityProperty getProperty(String name) {
        return properties.get(name);
    }

    public int getInt(int index) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return 0;
        }
        return getIntInternal(property);
    }

    public int getInt(String name) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return 0;
        }
        return getIntInternal(property);
    }

    public int getInt(EntityProperty property) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return 0;
        }
        return getIntInternal(property);
    }

    private int getIntInternal(EntityProperty property) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.INT) {
            return 0;
        }

        int index = property.getIndex();
        int value;
        Number number = values[index];
        if (number != null) {
            value = number.intValue();
        } else {
            value = property.getDefaultIntValue();
        }
        return value;
    }

    public float getFloat(int index) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return 0;
        }
        return getFloatInternal(property);
    }

    public float getFloat(String name) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return 0;
        }
        return getFloatInternal(property);
    }

    public float getFloat(EntityProperty property) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return 0;
        }
        return getFloatInternal(property);
    }

    private float getFloatInternal(EntityProperty property) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.FLOAT) {
            return 0;
        }

        int index = property.getIndex();
        float value;
        Number number = values[index];
        if (number != null) {
            value = number.floatValue();
        } else {
            value = property.getDefaultFloatValue();
        }
        return value;
    }

    public boolean getBoolean(int index) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return false;
        }
        return getBooleanInternal(property);
    }

    public boolean getBoolean(String name) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return false;
        }
        return getBooleanInternal(property);
    }

    public boolean getBoolean(EntityProperty property) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return false;
        }
        return getBooleanInternal(property);
    }

    private boolean getBooleanInternal(EntityProperty property) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.BOOL) {
            return false;
        }

        int index = property.getIndex();
        int value;
        Number number = values[index];
        if (number != null) {
            value = number.intValue();
        } else {
            value = property.getDefaultIntValue();
        }
        return value != 0;
    }

    public String getEnum(int index) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return "";
        }
        return getEnumInternal(property);
    }

    public String getEnum(String name) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return "";
        }
        return getEnumInternal(property);
    }

    public String getEnum(EntityProperty property) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return "";
        }
        return getEnumInternal(property);
    }

    private String getEnumInternal(EntityProperty property) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.ENUM) {
            return "";
        }
        EnumEntityProperty enumProperty = (EnumEntityProperty) property;

        int index = property.getIndex();
        int value;
        Number number = values[index];
        if (number != null) {
            value = number.intValue();
        } else {
            value = property.getDefaultIntValue();
        }
        return enumProperty.getValues().get(value);
    }

    @Nullable
    public IntIntPair setInt(int index, int value) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return null;
        }
        return setIntInternal(property, value);
    }

    @Nullable
    public IntIntPair setInt(String name, int value) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return null;
        }
        return setIntInternal(property, value);
    }

    @Nullable
    public IntIntPair setInt(EntityProperty property, int value) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return null;
        }
        return setIntInternal(property, value);
    }

    @Nullable
    private IntIntPair setIntInternal(EntityProperty property, int value) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.INT) {
            return null;
        }
        value = Mth.clamp(value, property.getMinimumIntValue(), property.getMaximumIntValue());

        int index = property.getIndex();
        Number number = values[index];
        if (number != null) {
            int current = number.intValue();
            if (current == value) {
                return null;
            }
        }
        values[index] = value;
        return IntIntPair.of(index, value);
    }

    @Nullable
    public IntFloatPair setFloat(int index, float value) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return null;
        }
        return setFloatInternal(property, value);
    }

    @Nullable
    public IntFloatPair setFloat(String name, float value) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return null;
        }
        return setFloatInternal(property, value);
    }

    @Nullable
    public IntFloatPair setFloat(EntityProperty property, float value) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return null;
        }
        return setFloatInternal(property, value);
    }

    @Nullable
    private IntFloatPair setFloatInternal(EntityProperty property, float value) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.FLOAT) {
            return null;
        }
        value = Mth.clamp(value, property.getMinimumFloatValue(), property.getMaximumFloatValue());

        int index = property.getIndex();
        Number number = values[index];
        if (number != null) {
            float current = number.floatValue();
            if (current == value) {
                return null;
            }
        }
        values[index] = value;
        return IntFloatPair.of(index, value);
    }

    @Nullable
    public IntIntPair setBoolean(int index, boolean value) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return null;
        }
        return setBooleanInternal(property, value);
    }

    @Nullable
    public IntIntPair setBoolean(String name, boolean value) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return null;
        }
        return setBooleanInternal(property, value);
    }

    @Nullable
    public IntIntPair setBoolean(EntityProperty property, boolean value) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return null;
        }
        return setBooleanInternal(property, value);
    }

    @Nullable
    private IntIntPair setBooleanInternal(EntityProperty property, boolean value) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.BOOL) {
            return null;
        }
        int intValue = value ? 1 : 0;

        int index = property.getIndex();
        Number number = values[index];
        if (number != null) {
            int current = number.intValue();
            if (current == intValue) {
                return null;
            }
        }
        values[index] = intValue;
        return IntIntPair.of(index, intValue);
    }

    @Nullable
    public IntIntPair setEnum(int index, String value) {
        EntityProperty property = getProperty(index);
        if (property == null) {
            return null;
        }
        return setEnumInternal(property, value);
    }

    @Nullable
    public IntIntPair setEnum(String name, String value) {
        EntityProperty property = getProperty(name);
        if (property == null) {
            return null;
        }
        return setEnumInternal(property, value);
    }

    @Nullable
    public IntIntPair setEnum(EntityProperty property, String value) {
        int index = property.getIndex();
        if (property != getProperty(index)) {
            return null;
        }
        return setEnumInternal(property, value);
    }

    @Nullable
    private IntIntPair setEnumInternal(EntityProperty property, String value) {
        EntityPropertyType type = property.getType();
        if (type != EntityPropertyType.ENUM) {
            return null;
        }
        EnumEntityProperty enumProperty = (EnumEntityProperty) property;
        int intValue = enumProperty.getValues().indexOf(value);
        if (intValue == -1) {
            return null;
        }

        int index = property.getIndex();
        Number number = values[index];
        if (number != null) {
            int current = number.intValue();
            if (current == intValue) {
                return null;
            }
        }
        values[index] = intValue;
        return IntIntPair.of(index, intValue);
    }
}
