package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/2/4.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    public BlockEntityFlowerPot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        //TODO: upgrade
        /*if (!namedTag.contains("PlantBlock")) {
            namedTag.putCompound("PlantBlock", new CompoundTag()
                    .putString("name", GlobalBlockPalette.getNameByBlockId(BlockID.AIR))
                    .putShort("val", 0));
        }*/

        if (!namedTag.contains("item")) {
            namedTag.putShort("item", 0);
        }

        if (!namedTag.contains("data")) {
            if (namedTag.contains("mData")) {
                namedTag.putInt("data", namedTag.getInt("mData"));
                namedTag.remove("mData");
            } else {
                namedTag.putInt("data", 0);
            }
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_FLOWER_POT;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = getDefaultCompound(this, FLOWER_POT);

        int item = namedTag.getShort("item");
        if (item != Block.AIR) {
            tag.putShort("item", item)
                    .putInt("mData", this.namedTag.getInt("data"));
        }
        return tag;
    }

}
