package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;

public final class ExplicitItemUseHandPolicy {

    private static final int OFFHAND_INVENTORY_SLOT = 1;
    private static final int OFFHAND_HOTBAR_SLOT = 0;

    private ExplicitItemUseHandPolicy() {
    }

    public static boolean isOffhandSlotChangeAllowed(Player source, Item newItem) {
        if (source instanceof ExplicitItemUseHandAccess access
                && access.isExplicitItemUseHandClient()) {
            return access.isExplicitItemUseHandAllowed();
        }
        return newItem.canDualWield() || newItem.isNull();
    }

    public static boolean isActiveOffhandEquipmentEcho(Player source,
                                                        MobEquipmentPacket packet,
                                                        Item authoritativeItem) {
        return source.supportsExplicitItemUseHand()
                && source.isUsingItem()
                && source.getUsingItemHand() == ItemUseHand.OFF_HAND
                && packet.windowId == ContainerIds.OFFHAND
                && packet.hotbarSlot == OFFHAND_HOTBAR_SLOT
                && packet.inventorySlot == OFFHAND_INVENTORY_SLOT
                && authoritativeItem.equalsExact(packet.item)
                && source.isUsingSameItem(authoritativeItem);
    }

    public static boolean shouldCancelActiveOffhandUse(Player source) {
        return !source.supportsExplicitItemUseHand()
                && source.isUsingItem()
                && source.getUsingItemHand() == ItemUseHand.OFF_HAND;
    }
}
