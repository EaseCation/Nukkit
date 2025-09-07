package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

public class BlockEntityChemistryTable extends BlockEntitySpawnable {
    /**
     * item ID.
     * int, optional.
     */
    public static final String TAG_ITEM_ID = "itemId";
    /**
     * item aux value.
     * short, optional.
     */
    public static final String TAG_ITEM_AUX = "itemAux";
    /**
     * item count.
     * byte, optional.
     */
    public static final String TAG_ITEM_STACK = "itemStack";

    @Nullable
    protected Item item;

    public BlockEntityChemistryTable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.CHEMISTRY_TABLE;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains(TAG_ITEM_ID)) {
            item = Item.get(namedTag.getInt(TAG_ITEM_ID), namedTag.getShort(TAG_ITEM_AUX), namedTag.getByte(TAG_ITEM_STACK));
        } else {
            item = null;
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (item != null) {
            namedTag.putInt(TAG_ITEM_ID, item.getId());
            namedTag.putShort(TAG_ITEM_AUX, item.getDamage());
            namedTag.putByte(TAG_ITEM_STACK, item.getCount());
        } else {
            namedTag.remove(TAG_ITEM_ID);
            namedTag.remove(TAG_ITEM_AUX);
            namedTag.remove(TAG_ITEM_STACK);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.CHEMISTRY_TABLE;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = getDefaultCompound(this, CHEMISTRY_TABLE);
        return item == null ? tag : tag.putInt(TAG_ITEM_ID, item.getId())
                .putShort(TAG_ITEM_AUX, item.getDamage())
                .putByte(TAG_ITEM_STACK, item.getCount());
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        //TODO: ChemistryTableBlockActor::tick
        return false;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    public void setItem(@Nullable Item item) {
        this.item = item;
    }
}
