package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.ContainerIds;

import java.util.Collection;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInventory extends BaseInventory {

    protected int itemInHandIndex = 0;

    public PlayerInventory(EntityHumanType player) {
        super(player, InventoryType.PLAYER);
    }

    /**
     * Called when a client equips a hotbar inventorySlot. This method should not be used by plugins.
     * This method will call PlayerItemHeldEvent.
     *
     * @param slot hotbar slot Number of the hotbar slot to equip.
     * @return boolean if the equipment change was successful, false if not.
     */
    public boolean equipItem(int slot) {
        if (!isHotbarSlot(slot)) {
            this.sendContents((Player) this.getHolder());
            return false;
        }

        if (this.getHolder() instanceof Player) {
            Player player = (Player) this.getHolder();
            PlayerItemHeldEvent ev = new PlayerItemHeldEvent(player, this.getItem(slot), slot);
            this.getHolder().getLevel().getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                this.sendContents(this.getViewers());
                return false;
            }

            if (player.fishing != null) {
                if (!(this.getItem(slot).equals(player.fishing.rod))) {
                    player.stopFishing(false);
                }
            }
        }

        this.setHeldItemIndex(slot, false);
        return true;
    }

    private boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot < this.getHotbarSize();
    }

    public int getHeldItemIndex() {
        return this.itemInHandIndex;
    }

    public void setHeldItemIndex(int index) {
        setHeldItemIndex(index, true);
    }

    public void setHeldItemIndex(int index, boolean send) {
        if (isHotbarSlot(index)) {
            this.itemInHandIndex = index;

            if (this.getHolder() instanceof Player player && send) {
//                this.sendHeldItem(player);
                PlayerHotbarPacket packet = new PlayerHotbarPacket();
                packet.selectedHotbarSlot = index;
                packet.windowId = ContainerIds.INVENTORY;
                packet.selectHotbarSlot = true;
                player.dataPacket(packet);
            }

            this.sendHeldItem(this.getHolder().getViewers().values());
        }
    }

    public Item getItemInHand() {
        Item item = this.getItem(this.getHeldItemIndex());
        if (item != null) {
            return item;
        }
        return Items.air();
    }

    public boolean setItemInHand(Item item) {
        return this.setItem(this.getHeldItemIndex(), item);
    }

    public void sendHeldItem(Player... players) {
        Item item = this.getItemInHand();
        long entityId = this.getHolder().getId();

        for (Player player : players) {
            if (player == this.getHolder()) {
                this.sendSlot(this.getHeldItemIndex(), player);

                MobEquipmentPacket pk0 = new MobEquipmentPacket();
                pk0.item = item;
                pk0.inventorySlot = pk0.hotbarSlot = this.getHeldItemIndex();
                pk0.eid = entityId;
                player.dataPacket(pk0);
                continue;
            }

            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.item = item;
            pk.inventorySlot = pk.hotbarSlot = this.getHeldItemIndex();
            pk.eid = entityId;
            player.dataPacket(pk);
        }
    }

    public void sendHeldItem(Collection<Player> players) {
        this.sendHeldItem(players.toArray(new Player[0]));
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        EntityHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        super.onSlotChange(index, before, after, send);
    }

    public int getHotbarSize() {
        return 9;
    }

    @Override
    public void clearAll() {
        int limit = this.getSize();
        for (int index = 0; index < limit; ++index) {
            this.clear(index);
        }

        getHolder().getArmorInventory().clearAll();
        getHolder().getOffhandInventory().clearAll();
    }

    @Override
    public void sendContents(Player[] players) {
        Item[] slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            slots[i] = this.getItem(i);
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == ContainerIds.NONE || !player.spawned) {
                if (this.getHolder() != player) this.close(player);
                continue;
            }
            InventoryContentPacket pk = new InventoryContentPacket();
            pk.slots = slots;
            pk.inventoryId = id;
            player.dataPacket(pk);
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(index);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventorySlotPacket pk = new InventorySlotPacket();
                pk.slot = index;
                pk.item = item;
                pk.inventoryId = ContainerIds.INVENTORY;
                player.dataPacket(pk);
            } else {
                int id = player.getWindowId(this);
                if (id == ContainerIds.NONE) {
                    this.close(player);
                    continue;
                }
                InventorySlotPacket pk = new InventorySlotPacket();
                pk.slot = index;
                pk.item = item;
                pk.inventoryId = id;
                player.dataPacket(pk);
            }
        }
    }

    public void sendCreativeContents() {
        if (!(this.getHolder() instanceof Player)) {
            return;
        }
        Player p = (Player) this.getHolder();

        InventoryContentPacket pk = new InventoryContentPacket();
        pk.inventoryId = ContainerIds.CREATIVE;
        pk.slots = p.getCreativeItems().toArray(new Item[0]);
        p.dataPacket(pk);
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = this.getType().getNetworkType();
        pk.x = who.getFloorX();
        pk.y = who.getFloorY();
        pk.z = who.getFloorZ();
        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        int windowId = who.getWindowId(this);
        if (windowId != ContainerIds.NONE) {
            ContainerClosePacket pk = new ContainerClosePacket();
            pk.windowId = windowId;
            pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
            who.resetClosingWindowId(windowId);
            who.dataPacket(pk);
        }
        // player can never stop viewing their own inventory
        if (who != holder) {
            super.onClose(who);
        }
    }

    public void addItemOrDrop(Item... slots) {
        for (Item drop : addItem(slots)) {
            EntityHuman holder = getHolder();
            holder.level.dropItem(holder, drop);
        }
    }
}
