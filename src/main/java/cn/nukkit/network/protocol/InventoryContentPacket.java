package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class InventoryContentPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.INVENTORY_CONTENT_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int inventoryId;
    public Item[] slots = new Item[0];
    public int containerSlotType;
    @Nullable
    public Integer dynamicContainerId;
    @Nullable
    public Item storageItem;

    @Override
    public DataPacket clean() {
        this.slots = new Item[0];
        this.dynamicContainerId = null;
        this.storageItem = null;
        this.containerSlotType = 0;
        return super.clean();
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.inventoryId);
        this.putUnsignedVarInt(this.slots.length);
        for (Item slot : this.slots) {
            this.putSlot(slot);
        }
    }

    @Override
    public InventoryContentPacket clone() {
        InventoryContentPacket pk = (InventoryContentPacket) super.clone();
        pk.slots = this.slots.clone();
        return pk;
    }
}
