package cn.nukkit.block.state;

import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString(doNotUseGetters = true)
public class BlockLegacy {
    public final int id;
//    public final String name;

    private int bitsUsed;
    final BlockStateInstance[] states = new BlockStateInstance[BlockStates.STATE_COUNT];
    private final List<BlockStateInstance> validStates = new ArrayList<>();

    private BlockInstance[] permutations;
    private BlockInstance defaultState;

    public BlockLegacy(int id, String name) {
        this.id = id;
//        this.name = name;
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
        validStates.add(instance);
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

    public int getStateNullable(BlockState state, int meta) {
        BlockStateInstance instance = states[state.id];
        if (instance == null) {
            return -1;
        }
        return instance.get(meta);
    }

    public int getState(BlockState state, int meta) {
        int value = getStateNullable(state, meta);
        if (value == -1) {
            return 0;
        }
        return value;
    }

    public boolean hasState(BlockState state) {
        return states[state.id] != null;
    }

    public int getVariantCount() {
        return permutations.length;
    }

    @Nullable
    public BlockInstance getBlock(int meta) {
        if (meta >= permutations.length) {
            return null;
        }
        return permutations[meta];
    }

    public CompoundTag serialize(int meta) {
        BlockInstance instance = getBlock(meta);
        if (instance == null) {
            return defaultState.getStatesTag();
        }
        return instance.getStatesTag();
    }

    public int deserialize(CompoundTag states) {
        int meta = 0;
        for (BlockStateInstance instance : validStates) {
            int value = instance.state.fromNBT(states);
            if (value < 0 || value >= instance.variationCount) {
                continue;
            }
            meta |= value << instance.endBit;
        }
        return meta;
    }
}
