package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

//TODO
public class BlockEntityCreakingHeart extends BlockEntity {
    private int cooldown;

    public BlockEntityCreakingHeart(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        cooldown = namedTag.getInt("Cooldown", 20);

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Cooldown", cooldown);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CREAKING_HEART;
    }
}
