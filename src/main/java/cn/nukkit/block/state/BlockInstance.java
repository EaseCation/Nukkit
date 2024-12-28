package cn.nukkit.block.state;

import lombok.ToString;

@ToString(exclude = "legacyBlock")
public class BlockInstance {
    private final BlockLegacy legacyBlock;
    public final int meta;
    private final int[] stateValues;

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
}
