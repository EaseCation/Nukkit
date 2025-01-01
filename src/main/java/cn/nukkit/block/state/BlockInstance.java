package cn.nukkit.block.state;

import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

@ToString(exclude = "legacyBlock")
public class BlockInstance {
    private final BlockLegacy legacyBlock;
    public final int meta;
    private final int[] stateValues;
    private CompoundTag statesTag;

    public BlockInstance(BlockLegacy oldBlock, int auxDataVal, int[] stateValues) {
        legacyBlock = oldBlock;
        this.meta = auxDataVal;
        this.stateValues = stateValues;
    }

    public BlockInstance setState(BlockState state, int value) {
        return legacyBlock.setState(state, value, meta);
    }

    public BlockInstance setState(BlockState state, boolean value) {
        return setState(state, value ? 1 : 0);
    }

    public BlockInstance setState(BlockState state, Enum<?> value) {
        return setState(state, value.ordinal());
    }

    public int getState(BlockState state) {
        return legacyBlock.getState(state, meta);
    }

    public CompoundTag getStatesTag() {
        return getStatesTagUnsafe().copy();
    }

    public CompoundTag getStatesTagUnsafe() {
        CompoundTag states = statesTag;
        if (states == null) {
            states = new CompoundTag("states");
            for (int i = 0; i < stateValues.length; i++) {
                int value = stateValues[i];
                if (value == -1) {
                    continue;
                }
                legacyBlock.states[i].state.toNBT(states, value);
            }
            statesTag = states;
        }
        return states;
    }
}
