package cn.nukkit.entity.property;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@ToString(exclude = "properties")
public class EntityProperties implements Iterable<EntityProperty> {
    static final EntityProperties EMPTY = new EntityProperties(":");

    private final List<EntityProperty> properties = new ArrayList<>();
    private final Map<String, EntityProperty> byName = new HashMap<>();

    private final Pair<Int2IntMap, Int2FloatMap> defaultValues = Pair.of(new Int2IntOpenHashMap(), new Int2FloatOpenHashMap());
    private final CompoundTag nbt = new CompoundTag();

    EntityProperties(String entityIdentifier) {
        nbt.putString("type", entityIdentifier)
                .putList("properties", new ListTag<>());
    }

    void registerProperties(EntityProperty... properties) {
        ListTag<CompoundTag> propertiesTag = nbt.getList("properties", CompoundTag.class);
        for (EntityProperty property : properties) {
            if (byName.putIfAbsent(property.getName(), property) != null) {
                throw new IllegalArgumentException("Duplicate property '" + property.getName() + "' in " + nbt.getString("type"));
            }

            int index = this.properties.size();
            property.setIndex(index);
            this.properties.add(property);

            if (property.getType() != EntityPropertyType.FLOAT) {
                defaultValues.left().put(index, property.getDefaultIntValue());
            } else {
                defaultValues.right().put(index, property.getDefaultFloatValue());
            }

            propertiesTag.addCompound(property.toNBT());
        }
    }

    @Nullable
    public Pair<Int2IntMap, Int2FloatMap> getDefaultValues() {
        if (isEmpty()) {
            return null;
        }
        return defaultValues;
    }

    @Nullable
    public EntityProperty get(int index) {
        if (index < 0 || index >= properties.size()) {
            return null;
        }
        return properties.get(index);
    }

    @Nullable
    public EntityProperty get(String name) {
        return byName.get(name);
    }

    @Nullable
    public IntEntityProperty getInt(int index) {
        return (IntEntityProperty) get(index);
    }

    @Nullable
    public IntEntityProperty getInt(String name) {
        return (IntEntityProperty) get(name);
    }

    @Nullable
    public FloatEntityProperty getFloat(int index) {
        return (FloatEntityProperty) get(index);
    }

    @Nullable
    public FloatEntityProperty getFloat(String name) {
        return (FloatEntityProperty) get(name);
    }

    @Nullable
    public BooleanEntityProperty getBoolean(int index) {
        return (BooleanEntityProperty) get(index);
    }

    @Nullable
    public BooleanEntityProperty getBoolean(String name) {
        return (BooleanEntityProperty) get(name);
    }

    @Nullable
    public EnumEntityProperty getEnum(int index) {
        return (EnumEntityProperty) get(index);
    }

    @Nullable
    public EnumEntityProperty getEnum(String name) {
        return (EnumEntityProperty) get(name);
    }

    public CompoundTag toNBT() {
        return nbt;
    }

    public int size() {
        return properties.size();
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    @Override
    public Iterator<EntityProperty> iterator() {
        return properties.iterator();
    }

    @Override
    public void forEach(Consumer<? super EntityProperty> action) {
        properties.forEach(action);
    }

    @Override
    public Spliterator<EntityProperty> spliterator() {
        return properties.spliterator();
    }
}
