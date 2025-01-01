package cn.nukkit.block.state;

import lombok.ToString;

import javax.annotation.Nullable;
import java.util.List;

@ToString
public class BlockStateInstance {
    private static final int MAX_BITS = 16;

    private final int startBit;
    private final int numBits;
    final int endBit;
    final int variationCount;
    private final int mask;
    private final int lowerMask;
    final BlockState state;
    private final List<?> values;

    public BlockStateInstance(int startBit, int numBits, int variationCount, BlockState state) {
        this.startBit = numBits + startBit - 1;
        if (this.startBit >= MAX_BITS) {
            throw new IndexOutOfBoundsException("Too many variants");
        }
        this.numBits = numBits;
        this.endBit = startBit;
        this.variationCount = variationCount;
        this.mask = ((0xffff << (MAX_BITS - numBits)) & 0xffff) >> (MAX_BITS - 1 - this.startBit);
        this.lowerMask = 0xffff >> (MAX_BITS - numBits);
        this.state = state;
        this.values = state.getValues().subList(0, variationCount);
    }

    public List<?> getValues() {
        return values;
    }

    public boolean isValidData(int meta) {
        return (mask & meta) >> endBit < variationCount;
    }

    @Nullable
    public BlockInstance set(int meta, int value, BlockInstance[] permutations) {
        if (value < 0 || value >= variationCount) {
            return null;
        }
        int index = (value << endBit) | ~mask & meta;
        if (index >= permutations.length) {
            return null;
        }
        return permutations[index];
    }

    @Nullable
    public BlockInstance set(int meta, boolean value, BlockInstance[] permutations) {
        return set(meta, value ? 1 : 0, permutations);
    }

    @Nullable
    public BlockInstance set(int meta, Enum<?> value, BlockInstance[] permutations) {
        return set(meta, value.ordinal(), permutations);
    }

    public int get(int meta) {
        return (meta >> endBit) & lowerMask;
    }

    public boolean getBoolean(int meta) {
        return get(meta) == 1;
    }
}
