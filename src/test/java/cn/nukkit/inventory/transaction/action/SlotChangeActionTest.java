package cn.nukkit.inventory.transaction.action;

import cn.nukkit.inventory.ArmorInventory;
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

class SlotChangeActionTest {

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
}
