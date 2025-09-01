package cn.nukkit.block.state;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
public class StringBlockState extends BlockState {
    private final List<String> values;

    public StringBlockState(String name, String... values) {
        super(name, values.length);
        if (values.length < 2) {
            throw new IllegalArgumentException("must contain at least 2 elements");
        }
        this.values = List.of(values);
    }

    @Override
    public List<String> getValues() {
        return values;
    }

    @Override
    public void toNBT(CompoundTag tag, int val) {
        tag.putString(name, values.get(val));
    }

    @Override
    public int fromNBT(CompoundTag tag) {
        Tag stateTag = tag.get(name);
        if (!(stateTag instanceof StringTag nbt)) {
            return -1;
        }
        return values.indexOf(nbt.data);
    }

    public int indexOf(String value) {
        return values.indexOf(value);
    }

    public boolean contains(String value) {
        return values.contains(value);
    }
}
