package cn.nukkit.block.state;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.List;
import java.util.stream.Stream;

public class EnumBlockState<T extends Enum<?>> extends BlockState {
    private final List<T> values;
    private final List<String> stringValues;

    protected EnumBlockState(String name, T... values) {
        super(name, values.length);
        if (values.length < 2) {
            throw new IllegalArgumentException("must contain at least 2 elements");
        }
        this.values = List.of(values);
        this.stringValues = Stream.of(values)
                .map(Enum::toString)
                .toList();
    }

    @Override
    public List<T> getValues() {
        return values;
    }

    @Override
    public void toNBT(CompoundTag tag, int val) {
        tag.putString(name, stringValues.get(val));
    }

    @Override
    public int fromNBT(CompoundTag tag) {
        Tag stateTag = tag.get(name);
        if (!(stateTag instanceof StringTag nbt)) {
            return -1;
        }
        return stringValues.indexOf(nbt.data);
    }
}
