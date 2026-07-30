package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.item.ItemSwordIron;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class ExplicitItemUseHandPolicyTest {

    @Test
    void allowsAnyOffhandTargetForAuthorizedJavaClients() {
        Player player = explicitHandPlayer(true);

        assertTrue(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new ItemSwordIron()));
        assertTrue(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new ItemShield()));
        assertTrue(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new Item(Item.AIR)));
    }

    @Test
    void rejectsAllOffhandTargetsForUnauthorizedJavaClients() {
        Player player = explicitHandPlayer(false);

        assertFalse(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new ItemSwordIron()));
        assertFalse(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new ItemShield()));
        assertFalse(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new Item(Item.AIR)));
    }

    @Test
    void preservesNativeBedrockDualWieldRestrictions() {
        Player player = mock(Player.class);

        assertFalse(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new ItemSwordIron()));
        assertTrue(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new ItemShield()));
        assertTrue(ExplicitItemUseHandPolicy.isOffhandSlotChangeAllowed(player, new Item(Item.AIR)));
    }

    @Test
    void acceptsActiveOffhandEquipmentEcho() {
        EquipmentEchoFixture fixture = activeOffhandEquipmentEcho();

        assertTrue(ExplicitItemUseHandPolicy.isActiveOffhandEquipmentEcho(
                fixture.player, fixture.packet, fixture.authoritativeItem));
    }

    @Test
    void rejectsEchoWhenAnyGuardDoesNotMatch() {
        EquipmentEchoFixture fixture = activeOffhandEquipmentEcho();

        when(fixture.player.supportsExplicitItemUseHand()).thenReturn(false);
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        when(fixture.player.supportsExplicitItemUseHand()).thenReturn(true);

        when(fixture.player.isUsingItem()).thenReturn(false);
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        when(fixture.player.isUsingItem()).thenReturn(true);

        when(fixture.player.getUsingItemHand()).thenReturn(ItemUseHand.MAIN_HAND);
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        when(fixture.player.getUsingItemHand()).thenReturn(ItemUseHand.OFF_HAND);

        fixture.packet.windowId = ContainerIds.INVENTORY;
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        fixture.packet.windowId = ContainerIds.OFFHAND;

        fixture.packet.hotbarSlot = 1;
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        fixture.packet.hotbarSlot = 0;

        fixture.packet.inventorySlot = 0;
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        fixture.packet.inventorySlot = 1;

        fixture.packet.item = new ItemSwordIron();
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
        fixture.packet.item = fixture.authoritativeItem.clone();

        when(fixture.player.isUsingSameItem(fixture.authoritativeItem)).thenReturn(false);
        assertFalse(isActiveOffhandEquipmentEcho(fixture));
    }

    @Test
    void cancelsOnlyActiveOffhandUseAfterCapabilityIsLost() {
        Player player = mock(Player.class);
        when(player.isUsingItem()).thenReturn(true);
        when(player.getUsingItemHand()).thenReturn(ItemUseHand.OFF_HAND);

        assertTrue(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(player));

        when(player.supportsExplicitItemUseHand()).thenReturn(true);
        assertFalse(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(player));
        when(player.supportsExplicitItemUseHand()).thenReturn(false);

        when(player.isUsingItem()).thenReturn(false);
        assertFalse(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(player));
        when(player.isUsingItem()).thenReturn(true);

        when(player.getUsingItemHand()).thenReturn(ItemUseHand.MAIN_HAND);
        assertFalse(ExplicitItemUseHandPolicy.shouldCancelActiveOffhandUse(player));
    }

    private static Player explicitHandPlayer(boolean capabilityAllowed) {
        Player player = mock(Player.class, withSettings().extraInterfaces(ExplicitItemUseHandAccess.class));
        ExplicitItemUseHandAccess access = (ExplicitItemUseHandAccess) player;
        when(access.isExplicitItemUseHandClient()).thenReturn(true);
        when(access.isExplicitItemUseHandAllowed()).thenReturn(capabilityAllowed);
        return player;
    }

    private static EquipmentEchoFixture activeOffhandEquipmentEcho() {
        Player player = mock(Player.class);
        Item authoritativeItem = new ItemShield();
        MobEquipmentPacket packet = new MobEquipmentPacket();
        packet.windowId = ContainerIds.OFFHAND;
        packet.hotbarSlot = 0;
        packet.inventorySlot = 1;
        packet.item = authoritativeItem.clone();
        when(player.supportsExplicitItemUseHand()).thenReturn(true);
        when(player.isUsingItem()).thenReturn(true);
        when(player.getUsingItemHand()).thenReturn(ItemUseHand.OFF_HAND);
        when(player.isUsingSameItem(authoritativeItem)).thenReturn(true);
        return new EquipmentEchoFixture(player, packet, authoritativeItem);
    }

    private static boolean isActiveOffhandEquipmentEcho(EquipmentEchoFixture fixture) {
        return ExplicitItemUseHandPolicy.isActiveOffhandEquipmentEcho(
                fixture.player, fixture.packet, fixture.authoritativeItem);
    }

    private record EquipmentEchoFixture(Player player, MobEquipmentPacket packet, Item authoritativeItem) {
    }
}
