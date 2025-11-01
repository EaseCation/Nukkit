package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.MinecartHopperInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.loot.LootTable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.LootTables;
import cn.nukkit.loot.Lootable;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.ContainerType;
import cn.nukkit.utils.MinecartType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class EntityMinecartHopper extends EntityMinecartAbstract implements InventoryHolder, Lootable {

    public static final int NETWORK_ID = EntityID.HOPPER_MINECART;

    protected MinecartHopperInventory inventory;

    @Nullable
    protected String lootTable;
    protected int lootTableSeed;

    public EntityMinecartHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setDisplayBlock(Block.get(Block.BLOCK_HOPPER), false);
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(5);
    }

    @Override
    public boolean isRideable(){
        return false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        unpackLootTable();

        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
            if (damager instanceof Player && ((Player) damager).isCreative()) {
                return;
            }
        }
        this.level.dropItem(this, Item.get(Item.HOPPER_MINECART));
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        unpackLootTable();

        player.addWindow(this.inventory);
        return false; // If true, the count of items player has in hand decreases
    }

    @Override
    public MinecartHopperInventory getInventory() {
        return inventory;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.inventory = new MinecartHopperInventory(this);
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

        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, ContainerType.MINECART_HOPPER)
                .putInt(DATA_CONTAINER_BASE_SIZE, this.inventory.getSize());
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        ListTag<CompoundTag> items = new ListTag<>("Items");
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
    public void close() {
        if (isClosed()) {
            return;
        }

        for (Player player : new ObjectArrayList<>(inventory.getViewers())) {
            player.removeWindow(inventory);
        }

        super.close();
    }

    @Override
    public boolean canDoInteraction(Player player) {
        return true;
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.opencontainer";
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
