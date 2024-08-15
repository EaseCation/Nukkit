package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DoubleChestInventory extends ContainerInventory implements InventoryHolder {

    private final ChestInventory left;
    private final ChestInventory right;

    public DoubleChestInventory(BlockEntityChest left, BlockEntityChest right) {
        super(null, InventoryType.DOUBLE_CHEST);
        this.holder = this;

        this.left = left.getRealInventory();
        this.left.setDoubleInventory(this);

        this.right = right.getRealInventory();
        this.right.setDoubleInventory(this);
/*
        Int2ObjectMap<Item> items = new Int2ObjectOpenHashMap<>();
        // First we add the items from the left chest
        Int2ObjectMap<Item> leftItems = this.left.getContents();
        for (int idx = 0; idx < this.left.getSize(); idx++) {
            Item item = leftItems.get(idx);
            if (item != null) { // Don't forget to skip empty slots!
                items.put(idx, item);
            }
        }
        // And them the items from the right chest
        Int2ObjectMap<Item> rightItems = this.right.getContents();
        for (int idx = 0; idx < this.right.getSize(); idx++) {
            Item item = rightItems.get(idx);
            if (item != null) { // Don't forget to skip empty slots!
                items.put(idx + this.left.getSize(), item); // idx + this.left.getSize() so we don't overlap left chest items
            }
        }

        this.setContents(items);
*/
    }

    @Override
    public Inventory getInventory() {
        return this;
    }

    @Override
    public BlockEntityChest getHolder() {
        return this.left.getHolder();
    }

    @Override
    public Item getItem(int index) {
        return index < this.left.getSize() ? this.left.getItem(index) : this.right.getItem(index - this.right.getSize());
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return index < this.left.getSize() ? this.left.setItem(index, item, send) : this.right.setItem(index - this.right.getSize(), item, send);
    }

    @Override
    public boolean clear(int index, boolean send) {
        return index < this.left.getSize() ? this.left.clear(index, send) : this.right.clear(index - this.right.getSize(), send);
    }

    @Override
    public Int2ObjectMap<Item> getContents() {
        Int2ObjectMap<Item> contents = new Int2ObjectOpenHashMap<>();

        for (int i = 0; i < this.getSize(); ++i) {
            contents.put(i, this.getItem(i));
        }

        return contents;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        /*if (items.size() > this.size) {
            Int2ObjectMap<Item> newItems = new Int2ObjectOpenHashMap<>();
            for (int i = 0; i < this.size; i++) {
                newItems.put(i, items.get(i));
            }
            items = newItems;
        }*/

        for (int i = 0; i < this.size; i++) {
            if (!items.containsKey(i)) {
                if (i < this.left.size) {
                    if (this.left.slots.containsKey(i)) {
                        this.clear(i);
                    }
                } else if (this.right.slots.containsKey(i - this.left.size)) {
                    this.clear(i);
                }
            } else if (!this.setItem(i, items.get(i))) {
                this.clear(i);
            }
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        this.left.viewers.add(who);
        this.right.viewers.add(who);

        if (this.getViewers().size() == 1) {
            BlockEventPacket pk1 = new BlockEventPacket();
            pk1.x = (int) this.left.getHolder().getX();
            pk1.y = (int) this.left.getHolder().getY();
            pk1.z = (int) this.left.getHolder().getZ();
            pk1.eventType = 1;
            pk1.eventData = 1;
            Level level = this.left.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.left.getHolder().blockCenter(), LevelSoundEventPacket.SOUND_CHEST_OPEN);
                level.addChunkPacket((int) this.left.getHolder().getX() >> 4, (int) this.left.getHolder().getZ() >> 4, pk1);
            }

            BlockEventPacket pk2 = new BlockEventPacket();
            pk2.x = (int) this.right.getHolder().getX();
            pk2.y = (int) this.right.getHolder().getY();
            pk2.z = (int) this.right.getHolder().getZ();
            pk2.eventType = 1;
            pk2.eventData = 1;

            level = this.right.getHolder().getLevel();
            if (level != null) {
//                level.addLevelSoundEvent(this.right.getHolder().blockCenter(), LevelSoundEventPacket.SOUND_CHEST_OPEN);
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk2);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEventPacket pk1 = new BlockEventPacket();
            pk1.x = (int) this.right.getHolder().getX();
            pk1.y = (int) this.right.getHolder().getY();
            pk1.z = (int) this.right.getHolder().getZ();
            pk1.eventType = 1;
            pk1.eventData = 0;

            Level level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addLevelSoundEvent(this.right.getHolder().blockCenter(), LevelSoundEventPacket.SOUND_CHEST_CLOSED);
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk1);
            }

            BlockEventPacket pk2 = new BlockEventPacket();
            pk2.x = (int) this.left.getHolder().getX();
            pk2.y = (int) this.left.getHolder().getY();
            pk2.z = (int) this.left.getHolder().getZ();
            pk2.eventType = 1;
            pk2.eventData = 0;

            level = this.left.getHolder().getLevel();
            if (level != null) {
//                level.addLevelSoundEvent(this.left.getHolder().blockCenter(), LevelSoundEventPacket.SOUND_CHEST_CLOSED);
                level.addChunkPacket((int) this.left.getHolder().getX() >> 4, (int) this.left.getHolder().getZ() >> 4, pk2);
            }
        }

        this.left.viewers.remove(who);
        this.right.viewers.remove(who);
        super.onClose(who);
    }

    public ChestInventory getLeftSide() {
        return this.left;
    }

    public ChestInventory getRightSide() {
        return this.right;
    }

    public void sendSlot(Inventory inv, int index, Player... players) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.slot = inv == this.right ? this.left.getSize() + index : index;
        pk.item = inv.getItem(index);

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }
}
