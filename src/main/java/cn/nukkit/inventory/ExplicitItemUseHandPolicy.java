package cn.nukkit.inventory;

import cn.nukkit.network.protocol.types.ContainerIds;

public final class ExplicitItemUseHandPolicy {

    private static final int OFFHAND_INVENTORY_SLOT = 1;
    private static final int OFFHAND_HOTBAR_SLOT = 0;

    private ExplicitItemUseHandPolicy() {
    }

    public static boolean isOffhandInventoryMutationAllowed(boolean explicitHandClient,
                                                             boolean capabilityAllowed) {
        return !explicitHandClient || capabilityAllowed;
    }

    public static boolean isHarmlessOffhandEquipmentEcho(boolean capabilityAllowed,
                                                          boolean usingItem,
                                                          ItemUseHand usingHand,
                                                          int windowId,
                                                          int hotbarSlot,
                                                          int inventorySlot,
                                                          boolean equipmentItemExactlyMatches,
                                                          boolean snapshotMatches) {
        return capabilityAllowed
                && usingItem
                && usingHand == ItemUseHand.OFF_HAND
                && windowId == ContainerIds.OFFHAND
                && hotbarSlot == OFFHAND_HOTBAR_SLOT
                && inventorySlot == OFFHAND_INVENTORY_SLOT
                && equipmentItemExactlyMatches
                && snapshotMatches;
    }

    public static boolean shouldCancelActiveOffhandUse(boolean capabilityAllowed,
                                                        boolean usingItem,
                                                        ItemUseHand usingHand) {
        return !capabilityAllowed && usingItem && usingHand == ItemUseHand.OFF_HAND;
    }
}
