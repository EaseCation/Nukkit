package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;

public class PlayerOffhandInventory extends BaseInventory {

    public PlayerOffhandInventory(EntityHumanType holder) {
        super(holder, InventoryType.OFFHAND);
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("Offhand can only carry one item at a time");
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        notifyAuthoritativeSlotChange(index);

        EntityHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        sendContents(false, getViewers().toArray(Player[]::new));
        sendContents(false, holder.getViewers().values().toArray(Player[]::new));

        int sound;
        if (after != null && (sound = after.getEquippingSound()) != -1 && !after.equals(before, false, false)) {
            holder.level.addLevelSoundEvent(holder, sound);
        }
    }

    @Override
    public void sendContents(Player... players) {
        sendContents(true, players);
    }

    private void sendContents(boolean syncOwner, Player... players) {
        Item item = this.getItem(0);
        long entityId = this.getHolder().getId();

        for (Player player : players) {
            if (player == this.getHolder()) {
                if (player.isServerAuthoritativeInventoryEnabled()) {
                    if (syncOwner) {
                        sendAuthoritativeContents(player);
                    }
                    continue;
                }
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.inventoryId = ContainerIds.OFFHAND;
                pk2.slots = new Item[]{item};
                player.dataPacket(pk2);
            } else {
                MobEquipmentPacket pk = new MobEquipmentPacket();
                pk.eid = entityId;
                pk.item = item;
                pk.inventorySlot = 1;
                pk.windowId = ContainerIds.OFFHAND;
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(0);
        long entityId = this.getHolder().getId();

        for (Player player : players) {
            if (player == this.getHolder()) {
                if (player.isServerAuthoritativeInventoryEnabled()) {
                    sendAuthoritativeSlot(0, player);
                    continue;
                }
                InventorySlotPacket pk2 = new InventorySlotPacket();
                pk2.inventoryId = ContainerIds.OFFHAND;
                pk2.item = item;
                player.dataPacket(pk2);
            } else {
                MobEquipmentPacket pk = new MobEquipmentPacket();
                pk.eid = entityId;
                pk.item = item;
                pk.inventorySlot = 1;
                pk.windowId = ContainerIds.OFFHAND;
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }

    public Item getItem() {
        return getItem(0);
    }

    public void setItem(Item item) {
        setItem(0, item);
    }
}
