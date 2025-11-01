package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.loot.LootTable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.LootTables;
import cn.nukkit.loot.Lootable;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockEntityAbstractContainer extends BlockEntitySpawnable implements BlockEntityContainer, InventoryHolder, BlockEntityNameable, Lootable {
    @Nullable
    private String lootTable;
    private int lootTableSeed;

    protected BlockEntityAbstractContainer(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        ListTag<CompoundTag> items = namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (items == null) {
            namedTag.putList(new ListTag<>("Items"));
        } else {
            Int2ObjectMap<Item> slots = getRealInventory().getContentsUnsafe();
            Iterator<CompoundTag> iter = items.iterator();
            while (iter.hasNext()) {
                CompoundTag tag = iter.next();

                int slot = tag.getByte("Slot");
                if (slot < 0 || slot >= getSize()) {
                    iter.remove();
                    continue;
                }

                Item item = NBTIO.getItemHelper(tag);
                if (item.isNull()) {
                    iter.remove();
                    continue;
                }

                slots.put(slot, item);
            }
        }

        lootTable = namedTag.getString("LootTable", null);
        lootTableSeed = namedTag.getInt("LootTableSeed");

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        Int2ObjectMap<Item> slots = getRealInventory().getContentsUnsafe();
        ListTag<CompoundTag> items = new ListTag<>("Items");
        for (Int2ObjectMap.Entry<Item> entry : slots.int2ObjectEntrySet()) {
            int slot = entry.getIntKey();
            if (slot < 0 || slot >= getSize()) {
                continue;
            }

            Item item = entry.getValue();
            if (item == null || item.isNull()) {
                continue;
            }

            items.add(NBTIO.putItemHelper(item, slot));
        }
        namedTag.putList(items);

        if (lootTable != null) {
            namedTag.putString("LootTable", lootTable);
            namedTag.putInt("LootTableSeed", lootTableSeed);
        } else {
            namedTag.remove("LootTable");
            namedTag.remove("LootTableSeed");
        }
    }

    @Override
    public void onBreak() {
        unpackLootTable();

        Inventory inventory = getRealInventory();

        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }

        inventory.clearAll();
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        Inventory inventory = getRealInventory();
        for (Player player : new ObjectArrayList<>(inventory.getViewers())) {
            player.removeWindow(inventory);
        }

        super.close();
    }

    @Override
    public Item getItem(int index) {
        for (CompoundTag item : namedTag.getList("Items", CompoundTag.class)) {
            if (item.getByte("Slot") == index) {
                return NBTIO.getItemHelper(item);
            }
        }

        return Items.air();
    }

    @Override
    public void setItem(int index, Item item) {
        ListTag<CompoundTag> items = namedTag.getList("Items", CompoundTag.class);

        for (int i = 0; i < items.size(); i++) {
            CompoundTag tag = items.get(i);
            if (tag.getByte("Slot") == index) {
                if (item.isNull()) {
                    items.remove(i);
                } else {
                    items.add(i, NBTIO.putItemHelper(item, index));
                }
                break;
            }
        }

        if (!item.isNull()) {
            items.add(NBTIO.putItemHelper(item, index));
        }
    }

    @Override
    public void unpackLootTable() {
        if (lootTable == null) {
            return;
        }
        LootTable table = LootTables.lookupByName(lootTable);
        if (table == null) {
            lootTable = null;
            return;
        }
        int seed = lootTableSeed;
        if (seed == 0) {
            seed = ThreadLocalRandom.current().nextInt();
        }
        table.fill(getRealInventory(), new NukkitRandom(seed), LootTableContext.builder(level).build());
        lootTable = null;
    }

    @Override
    public String getLootTable() {
        return lootTable;
    }

    @Override
    public void setLootTable(String lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public int getLootTableSeed() {
        return lootTableSeed;
    }

    @Override
    public void setLootTableSeed(int seed) {
        this.lootTableSeed = seed;
    }
}
