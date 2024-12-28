package cn.nukkit.block.state;

import cn.nukkit.math.Mth;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Arrays;

@ToString(doNotUseGetters = true)
public class BlockLegacy {
    public final int id;
    public final String name;

    private int bitsUsed;
    private final BlockStateInstance[] states = new BlockStateInstance[BlockStates.STATE_COUNT];

    private BlockInstance[] permutations;
    private BlockInstance defaultState;

    public BlockLegacy(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public BlockLegacy addState(BlockState state) {
        return addState(state, state.variationCount);
    }

    public BlockLegacy addState(BlockState state, int variationCount) {
        if (variationCount < 2) {
            throw new IllegalArgumentException("variationCount must be at least 2");
        }
        int numBits = Mth.ceillog2(variationCount);
        BlockStateInstance instance = new BlockStateInstance(bitsUsed, numBits, variationCount, state);
        states[state.id] = instance;
        bitsUsed += numBits;
        return this;
    }

    public BlockLegacy createPermutations() {
        int numStates = Math.max(1, (int) Math.pow(2, Math.min(bitsUsed, 16)));
        permutations = new BlockInstance[numStates];
        PERMUTATION:
        for (int i = 0; i < numStates; i++) {
            int[] stateValues = new int[BlockStates.STATE_COUNT];
            Arrays.fill(stateValues, -1);
            for (BlockStateInstance instance : states) {
                if (instance == null) {
                    continue;
                }
                if (!instance.isValidData(i)) {
                    continue PERMUTATION;
                }
                stateValues[instance.state.id] = instance.get(i);
            }
            permutations[i] = new BlockInstance(this, i, stateValues);
        }
        return this;
    }

    public void setDefaultState(BlockInstance state) {
        defaultState = state;
    }

    public BlockInstance getDefaultState() {
        if (defaultState == null) {
            return permutations[0];
        }
        return defaultState;
    }

    @Nullable
    public BlockInstance setStateNullable(BlockState state, int value, int meta) {
        BlockStateInstance instance = states[state.id];
        if (instance == null) {
            return null;
        }
        return instance.set(meta, value, permutations);
    }

    public BlockInstance setState(BlockState state, int value, int meta) {
        BlockInstance block = setStateNullable(state, value, meta);
        if (block == null) {
            return getDefaultState();
        }
        return block;
    }

    public BlockInstance setState(BlockState state, boolean value, int meta) {
        return setState(state, value ? 1 : 0, meta);
    }

    public BlockInstance setState(BlockState state, Enum<?> value, int meta) {
        return setState(state, value.ordinal(), meta);
    }

    public int getState(BlockState state, int meta) {
        return states[state.id].get(meta);
    }
}
