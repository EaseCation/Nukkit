package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.inventory.BoatChestInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.ContainerType;

public class EntityBoatChest extends EntityBoat implements InventoryHolder {

    public static final int NETWORK_ID = EntityID.CHEST_BOAT;

    private static final Vector3f SEAT_OFFSET = new Vector3f(0.2f, -0.2f, 0);
    private static final Vector3f PLAYER_SEAT_OFFSET = new Vector3f(0.2f, 1.02001f, 0);

    protected BoatChestInventory inventory;

    public EntityBoatChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.inventory = new BoatChestInventory(this);
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") instanceof ListTag) {
            ListTag<CompoundTag> items = this.namedTag.getList("Items", CompoundTag.class);
            for (CompoundTag item : items.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        this.dataProperties.putByte(DATA_CONTAINER_TYPE, ContainerType.CHEST_BOAT)
                .putInt(DATA_CONTAINER_BASE_SIZE, this.inventory.getSize());
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        if (this.inventory == null) {
            return;
        }
        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            Item item = this.inventory.getItem(slot);
            if (item == null || item.isNull()) {
                continue;
            }
            this.namedTag.getList("Items", CompoundTag.class)
                    .add(NBTIO.putItemHelper(item, slot));
        }
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    protected void dropItem() {
        super.dropItem();
        this.level.dropItem(this, Item.get(Item.CHEST));

        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
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
    public Vector3f getMountedOffset(Entity entity) {
        return entity instanceof Player ? PLAYER_SEAT_OFFSET : SEAT_OFFSET;
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
        return !this.isFull() && !player.isSneaking() ? "action.interact.ride.boat" : "action.interact.opencontainer";
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
