package cn.nukkit.inventory;

import cn.nukkit.network.protocol.types.ContainerIds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplicitItemUseHandPolicyTest {

    @Test
    void acceptsOnlyAnExactHarmlessOffhandEquipmentEcho() {
        assertTrue(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 0, 1, true, true));
    }

    @Test
    void rejectsEchoWhenAnyGuardDoesNotMatch() {
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                false, true, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 0, 1, true, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, false, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 0, 1, true, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.MAIN_HAND,
                ContainerIds.OFFHAND, 0, 1, true, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.OFF_HAND,
                ContainerIds.INVENTORY, 0, 1, true, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 1, 1, true, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 0, 0, true, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 0, 1, false, true));
        assertFalse(ExplicitItemUseHandPolicy.isHarmlessOffhandEquipmentEcho(
                true, true, ItemUseHand.OFF_HAND,
                ContainerIds.OFFHAND, 0, 1, true, false));
    }

    @Test
    void cancelsOnlyActiveOffhandUseAfterCapabilityIsLost() {
        assertTrue(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(
                false, true, ItemUseHand.OFF_HAND));
        assertFalse(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(
                true, true, ItemUseHand.OFF_HAND));
        assertFalse(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(
                false, false, ItemUseHand.OFF_HAND));
        assertFalse(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(
                false, true, ItemUseHand.MAIN_HAND));
    }
}
