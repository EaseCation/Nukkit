package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.ArmorInventory;
import cn.nukkit.inventory.ExplicitItemUseHandAccess;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.ItemBootsIron;
import cn.nukkit.item.ItemChestplateIron;
import cn.nukkit.item.ItemElytra;
import cn.nukkit.item.ItemHelmetIron;
import cn.nukkit.item.ItemLeggingsIron;
import cn.nukkit.item.ItemSwordIron;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class SlotChangeActionTest {

    @Test
    void acceptsNonDualWieldTargetForAuthorizedJavaClient() {
        Player player = explicitHandPlayer(true);
        Item source = new Item(Item.AIR);
        SlotChangeAction action = offhandAction(source, new ItemSwordIron());

        assertTrue(action.isValid(player));
    }

    @Test
    void rejectsNonDualWieldTargetForNativeBedrockClient() {
        Player player = mock(Player.class);
        Item source = new Item(Item.AIR);
        SlotChangeAction action = offhandAction(source, new ItemSwordIron());

        assertFalse(action.isValid(player));
    }

    @Test
    void rejectsOffhandMutationForUnauthorizedJavaClient() {
        Player player = explicitHandPlayer(false);
        Item source = new Item(Item.AIR);
        SlotChangeAction action = offhandAction(source, new ItemSwordIron());

        assertFalse(action.isValid(player));
    }

    @Test
    void acceptsOnlyMatchingArmorSlots() {
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_HEAD, new ItemHelmetIron()));
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_TORSO, new ItemChestplateIron()));
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_LEGS, new ItemLeggingsIron()));
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_FEET, new ItemBootsIron()));

        assertFalse(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_HEAD, new ItemChestplateIron()));
        assertFalse(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_TORSO, new ItemLeggingsIron()));
        assertFalse(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_LEGS, new ItemBootsIron()));
        assertFalse(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_FEET, new ItemHelmetIron()));
    }

    @Test
    void rejectsWeaponsAndStackedArmor() {
        Item sword = new ItemSwordIron();
        for (int slot = ArmorInventory.SLOT_HEAD; slot <= ArmorInventory.SLOT_FEET; slot++) {
            assertFalse(SlotChangeAction.isItemValidForArmorSlot(slot, sword));
        }

        assertFalse(SlotChangeAction.isItemValidForArmorSlot(
                ArmorInventory.SLOT_TORSO, new ItemChestplateIron(0, 2)));
    }

    @Test
    void preservesVanillaWearablesAndRemoval() {
        Item skull = new Item(1) {
            @Override
            public boolean isSkull() {
                return true;
            }
        };

        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_HEAD, skull));
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(
                ArmorInventory.SLOT_HEAD, new Item(ItemBlockID.CARVED_PUMPKIN)));
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_TORSO, new ItemElytra()));
        assertTrue(SlotChangeAction.isItemValidForArmorSlot(ArmorInventory.SLOT_HEAD, new Item(Item.AIR, 0, 0)));
    }

    private static Player explicitHandPlayer(boolean capabilityAllowed) {
        Player player = mock(Player.class, withSettings().extraInterfaces(ExplicitItemUseHandAccess.class));
        ExplicitItemUseHandAccess access = (ExplicitItemUseHandAccess) player;
        when(access.isExplicitItemUseHandClient()).thenReturn(true);
        when(access.isExplicitItemUseHandAllowed()).thenReturn(capabilityAllowed);
        return player;
    }

    private static SlotChangeAction offhandAction(Item source, Item target) {
        PlayerOffhandInventory inventory = mock(PlayerOffhandInventory.class);
        when(inventory.getItem(0)).thenReturn(source);
        when(inventory.getMaxStackSize()).thenReturn(64);
        return new SlotChangeAction(inventory, 0, source, target);
    }
}
