package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSerializer;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

/**
 * Created by Snake1999 on 2016/2/4.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    @Nullable
    private Block plant;

    public BlockEntityFlowerPot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        Block plantBlock = null;
        CompoundTag plantTag;
        if (namedTag.contains("item")) {
            int id = namedTag.getShort("item");
            if (id != Item.AIR) {
                int meta;
                if (namedTag.contains("mData")) {
                    meta = namedTag.getInt("mData");
                    namedTag.remove("mData");
                } else {
                    meta = namedTag.getInt("data");
                    namedTag.remove("data");
                }

                plantBlock = NBTIO.getBlockHelper(new CompoundTag()
                        .putString("name", GlobalBlockPalette.getNameByBlockId(Block.itemIdToBlockId(id)))
                        .putShort("val", meta));

                if (plantBlock.isAir() || plantBlock.getId() == Block.INFO_UPDATE) {
                    plantBlock = null;
                } else {
                    namedTag.putCompound("PlantBlock", BlockSerializer.serialize(plantBlock));
                }

                namedTag.remove("item");
            }
        } else if ((plantTag = namedTag.getCompound("PlantBlock", null)) != null) {
            plantBlock = NBTIO.getBlockHelper(plantTag);

            if (plantBlock.isAir()) {
                plantBlock = null;
            }
        }
        plant = plantBlock;

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (!hasPlant()) {
            namedTag.remove("PlantBlock");
            return;
        }

        namedTag.putCompound("PlantBlock", BlockSerializer.serialize(plant));
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_FLOWER_POT;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = getDefaultCompound(this, FLOWER_POT);

        if (hasPlant()) {
            tag.putCompound("PlantBlock", BlockSerializer.serialize(plant));
        }

        return tag;
    }

    @Nullable
    public Block getPlant() {
        return plant;
    }

    public void setPlant(@Nullable Block plantBlock) {
        plant = plantBlock;

        setDirty();
    }

    public boolean hasPlant() {
        return plant != null && !plant.isAir();
    }
}
