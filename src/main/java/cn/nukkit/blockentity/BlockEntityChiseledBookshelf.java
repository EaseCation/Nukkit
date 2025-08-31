package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import javax.annotation.Nullable;

public class BlockEntityChiseledBookshelf extends BlockEntitySpawnable implements HopperInteractable {
    public static final int SLOT_COUNT = 6;

    private Item[] items;
    private int lastInteractedSlot;

    public BlockEntityChiseledBookshelf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        items = new Item[SLOT_COUNT];

        ListTag<CompoundTag> books =  namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (books != null) {
            for (int i = 0; i < SLOT_COUNT && i < books.size(); i++) {
                Item item = NBTIO.getItemHelper(books.get(i));

                if (item.isNull()) {
                    continue;
                }

                items[i] = item;
            }

            lastInteractedSlot = namedTag.getInt("LastInteractedSlot", -1);
        } else {
            lastInteractedSlot = -1;
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        boolean hasBooks = false;

        ListTag<CompoundTag> books = new ListTag<>();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            Item item = getItem(slot);

            if (item != null && !item.isNull()) {
                hasBooks = true;
            }

            books.add(NBTIO.putItemHelper(item));
        }

        if (!hasBooks) {
            namedTag.remove("Items");
            namedTag.remove("LastInteractedSlot");
            return;
        }
        namedTag.putList("Items", books);

        if (lastInteractedSlot < 0) {
            namedTag.remove("LastInteractedSlot");
            return;
        }
        namedTag.putInt("LastInteractedSlot", lastInteractedSlot);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CHISELED_BOOKSHELF;
    }

    @Override
    public void onBreak() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);
            if (item == null) {
                continue;
            }
            items[i] = null;

            level.dropItem(this, item);
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, CHISELED_BOOKSHELF);
/*
        boolean hasBooks = false;
        ListTag<CompoundTag> books = new ListTag<>();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            Item item = getItem(slot);

            if (item != null && !item.isNull()) {
                hasBooks = true;
            }

            books.add(NBTIO.putItemHelper(item));
        }

        if (hasBooks) {
            nbt.putList("Items", books)
                    .putInt("LastInteractedSlot", lastInteractedSlot);
        }
*/
        return nbt;
    }

    @Nullable
    public Item getItem(int index) {
        return items[index];
    }

    public void setItem(int index, @Nullable Item item) {
        items[index] = item;

        lastInteractedSlot = index;
    }

    public int getLastInteractedSlot() {
        return lastInteractedSlot;
    }
}
