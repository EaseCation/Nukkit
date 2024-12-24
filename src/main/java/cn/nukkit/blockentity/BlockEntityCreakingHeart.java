package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

//TODO
public class BlockEntityCreakingHeart extends BlockEntity {
    public BlockEntityCreakingHeart(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CREAKING_HEART;
    }
}
