package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.inventory.BoatChestInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.loot.LootTable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.LootTables;
import cn.nukkit.loot.Lootable;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.ContainerType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class EntityBoatChest extends EntityBoat implements InventoryHolder, Lootable {

    public static final int NETWORK_ID = EntityID.CHEST_BOAT;

    protected BoatChestInventory inventory;

    @Nullable
    protected String lootTable;
    protected int lootTableSeed;

    public EntityBoatChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.inventory = new BoatChestInventory(this);
        ListTag<CompoundTag> items = namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (items == null) {
            namedTag.putList(new ListTag<>("Items"));
        } else {
            Int2ObjectMap<Item> slots = inventory.getContentsUnsafe();
            Iterator<CompoundTag> iter = items.iterator();
            while (iter.hasNext()) {
                CompoundTag tag = iter.next();

                int slot = tag.getByte("Slot");
                if (slot < 0 || slot >= inventory.getSize()) {
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

        this.dataProperties.putByte(DATA_CONTAINER_TYPE, ContainerType.CHEST_BOAT)
                .putInt(DATA_CONTAINER_BASE_SIZE, this.inventory.getSize());
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        ListTag<CompoundTag> items = new ListTag<>();
        if (this.inventory != null) {
            Int2ObjectMap<Item> slots = inventory.getContentsUnsafe();
            for (Int2ObjectMap.Entry<Item> entry : slots.int2ObjectEntrySet()) {
                int slot = entry.getIntKey();
                if (slot < 0 || slot >= inventory.getSize()) {
                    continue;
                }

                Item item = entry.getValue();
                if (item == null || item.isNull()) {
                    continue;
                }

                items.add(NBTIO.putItemHelper(item, slot));
            }
        }
        namedTag.putList("Items", items);

        if (lootTable != null) {
            namedTag.putString("LootTable", lootTable);
            namedTag.putInt("LootTableSeed", lootTableSeed);
        } else {
            namedTag.remove("LootTable");
            namedTag.remove("LootTableSeed");
        }
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    protected void dropItem() {
        unpackLootTable();

        super.dropItem();
        this.level.dropItem(this, Item.get(ItemBlockID.CHEST));

        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        unpackLootTable();

        if (this.isFull() || player.isSneaking()) {
            player.addWindow(this.inventory);
            return false;
        }

        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public boolean mountEntity(Entity entity) {
        if (this.isFull()) {
            return false;
        }
        return super.mountEntity(entity);
    }

    @Override
    public boolean isFull() {
        return !this.passengers.isEmpty();
    }

    @Override
    public boolean canDoInteraction(Player player) {
        return true;
    }

    @Override
    public String getInteractButtonText(Player player) {
        return canRide() && !this.isFull() && !player.isSneaking() ? "action.interact.ride.boat" : "action.interact.opencontainer";
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return super.getMountedOffset(entity).add(0.2f, 0, 0);
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
