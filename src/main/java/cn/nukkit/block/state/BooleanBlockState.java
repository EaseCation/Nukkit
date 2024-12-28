package cn.nukkit.block.state;

import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.booleans.BooleanImmutableList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import lombok.ToString;

@ToString(callSuper = true)
public class BooleanBlockState extends BlockState {
    private static final BooleanList VALUES = BooleanImmutableList.of(false, true);

    protected BooleanBlockState(String name) {
        super(name, 2);
    }

    @Override
    public BooleanList getValues() {
        return VALUES;
    }

    @Override
    public void toNBT(CompoundTag tag, int val) {
        tag.putBoolean(name, VALUES.getBoolean(val));
    }

    @Override
    public int fromNBT(CompoundTag tag) {
        Tag stateTag = tag.get(name);
        if (!(stateTag instanceof ByteTag nbt)) {
            return -1;
        }
        return nbt.data;
    }
}
