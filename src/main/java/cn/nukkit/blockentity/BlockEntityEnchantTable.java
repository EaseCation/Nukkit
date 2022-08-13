package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntityEnchantTable extends BlockEntitySpawnable implements BlockEntityNameable {

    public BlockEntityEnchantTable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.ENCHANTING_TABLE;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, ENCHANT_TABLE)
                .putFloat("rott", 0);

        if (this.hasName()) {
            nbt.put("CustomName", this.namedTag.get("CustomName"));
        }

        return nbt;
    }

}
