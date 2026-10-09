package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrossbow;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemSerializer;
import cn.nukkit.item.ItemUpgrader;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrossbowLoadItemSourceTest {
    @BeforeAll static void initialize() { Block.init(); Item.init(); Enchantment.init(); }

    @Test void normalLoadPaysOneArrowAndUpdatesTheSource() { check("normal", true, true, 1, 7, 1, 1); }
    @Test void cancelledDebitCannotLoad() { check("ammo-cancel", true, false, 0, 8, 0, 0); }
    @Test void modifiedDebitCannotLoad() { check("ammo-modify", true, false, 0, 8, 0, 0); }
    @Test void debitSelectionChangeLoadsOnlyTheOriginalBow() { check("ammo-slot", true, true, 1, 7, 1, 1); }
    @Test void replacedAmmoIsNotConsumedByTheOldAction() { check("ammo-replace", true, false, 0, 8, 0, 0); }
    @Test void removedAmmoCannotBeResurrected() { check("ammo-remove", true, false, 0, 0, 0, 0); }
    @Test void movedAmmoIsNotSearchedInAnotherSlot() { check("ammo-move", true, false, 0, 0, 0, 0); }
    @Test void bowReplacementAfterDebitWins() { check("ammo-bow-replace", true, false, 0, 7, 0, 0); }
    @Test void invalidationAfterDebitStopsRemainingEffects() { check("ammo-invalidate", true, false, 0, 7, 0, 0); }
    @Test void cancelledLoadWriteDoesNotPublishChargedState() { check("write-cancel", true, false, 0, 7, 0, 0); }
    @Test void modifiedLoadWriteDoesNotPublishChargedState() { check("write-modify", true, false, 0, 7, 0, 0); }
    @Test void nestedBowReplacementCannotBeOverwritten() { check("write-replace", true, false, 0, 7, 0, 0); }
    @Test void writeSelectionChangeKeepsOriginalSlot() { check("write-slot", true, true, 1, 7, 1, 1); }
    @Test void invalidationDuringWriteDoesNotPublishChargedState() { check("write-invalidate", true, false, 1, 7, 1, 0); }
    @Test void lastArrowIsConsumed() { check("last-arrow", true, true, 1, 0, 1, 1); }
    @Test void offhandArrowRetainsPriority() { check("offhand-arrow", true, true, 1, 8, 1, 1); }
    @Test void offhandFireworkRetainsPriorityAndDurabilityCost() { check("offhand-firework", true, true, 1, 8, 3, 1); }
    @Test void multishotPaysOneArrowForThreeLoadedProjectiles() { check("multi", true, true, 3, 7, 3, 1); }
    @Test void creativeLoadNeedsNoDebitOrDurability() { check("creative-empty", true, true, 1, 0, 0, 1); }
    @Test void alreadyChargedBowCannotPayAgain() { check("already-charged", true, false, 1, 8, 0, 0); }
    @Test void incompleteChargeRetainsBothSources() { check("short", true, false, 0, 8, 0, 0); }
    @Test void quickChargeUsesTheExistingDuration() { check("quick", true, true, 1, 7, 1, 1); }
    @Test void disabledModeRetainsCancelledDebitBehavior() { check("ammo-cancel", false, true, 1, 8, 1, 1); }
    @Test void disabledModeRetainsCurrentHandWrite() { check("ammo-slot", false, true, 0, 7, 0, 1); }

    private void check(String variant, boolean enabled, boolean accepted, int chargedCount, int arrows, int damage, int notifications) {
        // 库存与回调执行真实核心方法；只替代本核心测试没有的 Synapse 物品序列化。
        try (MockedStatic<ItemSerializer> serializers = mockStatic(ItemSerializer.class);
             MockedStatic<ItemUpgrader> upgraders = mockStatic(ItemUpgrader.class);
             MockedStatic<Server> servers = mockStatic(Server.class)) {
            serializers.when(() -> ItemSerializer.serializeItem(any())).thenAnswer(call -> {
                Item item = call.getArgument(0);
                return new CompoundTag().putShort("id", item.getId()).putShort("Damage", item.getDamage())
                        .putByte("Count", item.getCount());
            });
            serializers.when(() -> ItemSerializer.deserialize(any())).thenAnswer(call -> {
                CompoundTag tag = call.getArgument(0);
                return Item.get(tag.getShort("id"), tag.getShort("Damage"), tag.getByte("Count"));
            });
            Server server = mock(Server.class);
            PluginManager plugins = mock(PluginManager.class);
            when(server.getPluginManager()).thenReturn(plugins);
            servers.when(Server::getInstance).thenReturn(server);
            Player player = mock(Player.class);
            PlayerInventory inventory = new PlayerInventory(player);
            PlayerOffhandInventory offhand = new PlayerOffhandInventory(player);
            ItemCrossbow original = new ItemCrossbow();
            if (variant.equals("multi")) original.addEnchantment(Enchantment.getEnchantment(Enchantment.MULTISHOT).setLevel(1));
            if (variant.equals("quick")) original.addEnchantment(Enchantment.getEnchantment(Enchantment.QUICK_CHARGE).setLevel(3));
            if (variant.equals("already-charged")) original.setChargedItem(Item.get(ItemID.ARROW));
            inventory.slots.put(1, original);
            inventory.slots.put(2, Item.get(ItemID.ARROW, 0, variant.equals("last-arrow") ? 1 : 8));
            if (variant.equals("creative-empty")) inventory.slots.remove(2);
            if (variant.equals("offhand-arrow")) offhand.slots.put(0, Item.get(ItemID.ARROW, 0, 8));
            if (variant.equals("offhand-firework")) offhand.slots.put(0, Item.get(ItemID.FIREWORK_ROCKET, 0, 8));
            inventory.setHeldItemIndex(1, false);
            when(player.getInventory()).thenReturn(inventory);
            when(player.getOffhandInventory()).thenReturn(offhand);
            when(player.getViewers()).thenReturn(Map.of());
            when(player.isMainThreadInputEnabled()).thenReturn(enabled);
            when(player.isSurvivalLike()).thenReturn(!variant.equals("creative-empty"));
            when(player.isCreative()).thenReturn(variant.equals("creative-empty"));
            player.level = mock(Level.class);
            AtomicBoolean valid = new AtomicBoolean(true);
            when(player.canContinueItemUse(anyLong())).thenAnswer(call -> valid.get());
            doAnswer(call -> {
                if (!(call.getArgument(0) instanceof EntityInventoryChangeEvent event)) return null;
                if (event.getSlot() == 2) {
                    if (variant.equals("ammo-cancel")) event.setCancelled();
                    if (variant.equals("ammo-modify")) event.getNewItem().setCount(8);
                    if (variant.equals("ammo-slot")) inventory.setHeldItemIndex(0, false);
                    if (variant.equals("ammo-replace")) inventory.slots.put(2, inventory.slots.get(2).clone());
                    if (variant.equals("ammo-remove") || variant.equals("ammo-move")) inventory.slots.remove(2);
                    if (variant.equals("ammo-move")) inventory.slots.put(3, Item.get(ItemID.ARROW, 0, 8));
                    if (variant.equals("ammo-bow-replace")) inventory.slots.put(1, original.clone());
                    if (variant.equals("ammo-invalidate")) valid.set(false);
                } else if (event.getSlot() == 1) {
                    if (variant.equals("write-cancel")) event.setCancelled();
                    if (variant.equals("write-modify")) event.setNewItem(original.clone());
                    if (variant.equals("write-replace")) inventory.slots.put(1, original.clone());
                    if (variant.equals("write-slot")) inventory.setHeldItemIndex(0, false);
                    if (variant.equals("write-invalidate")) valid.set(false);
                }
                return null;
            }).when(plugins).callEvent(any());
            ItemCrossbow used = (ItemCrossbow) original.clone();
            assertEquals(accepted, used.onUse(player, variant.equals("short") ? 22 : variant.equals("quick") ? 8 : 25));
            ItemCrossbow actual = (ItemCrossbow) inventory.getItem(1);
            assertEquals(chargedCount, actual.getChargedItem().getCount());
            assertEquals(chargedCount == 0, actual.canContinueUsing());
            assertEquals(arrows, inventory.getItem(2).getCount());
            assertEquals(damage, actual.getDamage());
            verify(player, times(notifications)).dataPacket(argThat(packet -> packet instanceof EntityEventPacket event
                    && event.event == EntityEventPacket.CHARGED_ITEM));
            if (variant.equals("ammo-slot") && !enabled) {
                assertEquals(ItemID.CROSSBOW, inventory.getItem(0).getId());
                assertEquals(1, ((ItemCrossbow) inventory.getItem(0)).getChargedItem().getCount());
            } else assertEquals(ItemID.AIR, inventory.getItem(0).getId());
            if (variant.equals("ammo-move")) assertEquals(8, inventory.getItem(3).getCount());
            if (variant.startsWith("offhand")) assertEquals(7, offhand.getItem(0).getCount());
            if (variant.equals("offhand-firework")) assertEquals(ItemID.FIREWORK_ROCKET, actual.getChargedItem().getId());
        }
    }
}
