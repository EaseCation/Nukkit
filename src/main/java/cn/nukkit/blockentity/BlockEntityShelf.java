package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import javax.annotation.Nullable;

public class BlockEntityShelf extends BlockEntitySpawnable implements HopperInteractable {
    public static final int SLOT_COUNT = 3;

    private Item[] items;

    public BlockEntityShelf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.SHELF;
    }

    @Override
    protected void initBlockEntity() {
        items = new Item[SLOT_COUNT];

        ListTag<CompoundTag> items = namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (items != null) {
            for (int i = 0; i < SLOT_COUNT && i < items.size(); i++) {
                Item item = NBTIO.getItemHelper(items.get(i));
                if (item.isNull()) {
                    continue;
                }
                this.items[i] = item;
            }
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        boolean empty = true;
        ListTag<CompoundTag> items = new ListTag<>();
        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);
            if (item != null && !item.isNull()) {
                empty = false;
            }
            items.add(NBTIO.putItemHelper(item));
        }
        if (!empty) {
            namedTag.putList("Items", items);
        } else {
            namedTag.remove("Items");
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.OAK_SHELF
                || blockId == Block.SPRUCE_SHELF
                || blockId == Block.BIRCH_SHELF
                || blockId == Block.JUNGLE_SHELF
                || blockId == Block.ACACIA_SHELF
                || blockId == Block.DARK_OAK_SHELF
                || blockId == Block.MANGROVE_SHELF
                || blockId == Block.CHERRY_SHELF
                || blockId == Block.PALE_OAK_SHELF
                || blockId == Block.BAMBOO_SHELF
                || blockId == Block.CRIMSON_SHELF
                || blockId == Block.WARPED_SHELF;
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
        CompoundTag nbt = getDefaultCompound(this, SHELF);

        boolean empty = true;
        ListTag<CompoundTag> items = new ListTag<>();
        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);
            if (item != null && !item.isNull()) {
                empty = false;
            }
            items.add(NBTIO.putItemHelper(item));
        }
        if (!empty) {
            nbt.putList("Items", items);
        }

        return nbt;
    }

    @Nullable
    public Item getItem(int index) {
        return items[index];
    }

    public void setItem(int index, @Nullable Item item) {
        items[index] = item;
        setDirty();
    }

    private void setItemNonnull(int index, Item item) {
        if (item.isNull()) {
            item = null;
        }
        items[index] = item;
    }

    public void swapItems(Inventory inventory, int startSlot) {
        int centerSlot = startSlot + 1;
        int endSlot = startSlot + 2;
        Item left = inventory.getItem(startSlot);
        Item center = inventory.getItem(centerSlot);
        Item right = inventory.getItem(endSlot);
        inventory.setItem(startSlot, items[0]);
        inventory.setItem(centerSlot, items[1]);
        inventory.setItem(endSlot, items[2]);
        setItemNonnull(0, left);
        setItemNonnull(1, center);
        setItemNonnull(2, right);
        setDirty();
    }

    public void dropAllItems() {
        boolean dirty = false;

        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);

            if (item == null) {
                continue;
            }

            level.dropItem(this, item);

            items[i] = null;
            dirty = true;
        }

        if (dirty) {
            setDirty();
        }
    }

    public int getComparatorSignal() {
        int itemsStored = 0;
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (getItem(i) != null) {
                itemsStored |= 1 << i;
            }
        }
        return itemsStored;
    }
}
