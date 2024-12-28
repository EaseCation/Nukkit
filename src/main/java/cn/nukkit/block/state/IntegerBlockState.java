package cn.nukkit.block.state;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.ToString;

import java.util.stream.IntStream;

@ToString(callSuper = true)
public class IntegerBlockState extends BlockState {
    private final IntList values;

    protected IntegerBlockState(String name, int variationCount) {
        super(name, variationCount);
        if (variationCount < 2) {
            throw new IllegalArgumentException("variationCount must be at least 2");
        }
        values = IntImmutableList.toList(IntStream.range(0, variationCount));
    }

    @Override
    public IntList getValues() {
        return values;
    }

    @Override
    public void toNBT(CompoundTag tag, int val) {
        tag.putInt(name, values.getInt(val));
    }

    @Override
    public int fromNBT(CompoundTag tag) {
        Tag stateTag = tag.get(name);
        if (!(stateTag instanceof IntTag nbt)) {
            return -1;
        }
        return nbt.data;
    }
}
