package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrossbow;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemSerializer;
import cn.nukkit.item.ItemUpgrader;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrossbowFireItemSourceTest {
    @BeforeAll static void initialize() { Block.init(); Item.init(); }

    @Test void normalShotSettlesOneOriginalCrossbow() { check("normal", true, 1, false, false); }
    @Test void launchSelectionChangeCannotDuplicateOrFire() { check("launch-slot", true, 0, true, false); }
    @Test void equalValueReplacementDuringLaunchWins() { check("launch-replace", true, 0, true, false); }
    @Test void invalidatedLaunchCannotFireOrClearTheSource() { check("launch-invalidate", true, 0, true, false); }
    @Test void selectionChangeDuringSettlementWritesOnlyTheOriginalSlot() { check("write-slot", true, 1, false, false); }
    @Test void nestedReplacementDuringSettlementWins() { check("write-replace", true, 0, true, false); }
    @Test void cancelledSettlementCannotProduceAProjectile() { check("write-cancel", true, 0, true, false); }
    @Test void modifiedSettlementCannotProduceAProjectile() { check("write-modify", true, 0, true, false); }
    @Test void invalidationDuringSettlementPreventsPublication() { check("write-invalidate", true, 0, false, false); }
    @Test void cancelledLaunchRetainsExistingChargeConsumption() { check("cancel", true, 0, false, false); }
    @Test void multishotSettlesOnceBeforePublishingThreeArrows() { check("multi", true, 3, false, false); }
    @Test void secondLaunchSelectionChangeClosesAllPendingArrows() { check("multi-second-slot", true, 0, true, false); }
    @Test void oneCancelledMultishotArrowDoesNotCancelOtherArrows() { check("multi-second-cancel", true, 2, false, false); }
    @Test void invalidationWhilePublishingStopsTheRemainingArrows() { check("multi-publish-invalidate", true, 1, false, false); }
    @Test void offhandShotSettlesTheOriginalHand() { check("offhand-normal", true, 1, false, false); }
    @Test void offhandRouteChangeDuringLaunchStopsTheShot() { check("offhand-route", true, 0, true, false); }
    @Test void disabledModeRetainsExistingSelectionWrite() { check("launch-slot", false, 1, true, true); }
    @Test void disabledModeRetainsExistingCancelledSettlement() { check("write-cancel", false, 1, true, false); }
    @Test void fireworkShotSettlesBeforePublication() { check("firework-normal", true, 1, false, false); }
    @Test void cancelledFireworkSettlementCannotPublish() { check("firework-write-cancel", true, 0, true, false); }
    @Test void modifiedFireworkSettlementCannotPublish() { check("firework-write-modify", true, 0, true, false); }
    @Test void fireworkSettlementSelectionChangeKeepsOriginalSlot() { check("firework-write-slot", true, 1, false, false); }

    private void check(String variant, boolean inputMode, int published, boolean charged, boolean duplicate) {
        // 核心测试没有 Synapse 的运行时物品映射；此处只保存本用例的箭，不测试协议序列化。
        try (MockedStatic<ItemSerializer> serializers = mockStatic(ItemSerializer.class);
             MockedStatic<ItemUpgrader> upgraders = mockStatic(ItemUpgrader.class)) {
            serializers.when(() -> ItemSerializer.serializeItem(any())).thenAnswer(call -> {
                Item item = call.getArgument(0);
                return new CompoundTag().putShort("id", item.getId()).putShort("Damage", item.getDamage())
                        .putByte("Count", item.getCount());
            });
            serializers.when(() -> ItemSerializer.deserialize(any())).thenAnswer(call -> {
                CompoundTag tag = call.getArgument(0);
                return Item.get(tag.getShort("id"), tag.getShort("Damage"), tag.getByte("Count"));
            });
            checkSerializedArrow(variant, inputMode, published, charged, duplicate);
        }
    }

    private void checkSerializedArrow(String variant, boolean inputMode, int published, boolean charged, boolean duplicate) {
        boolean firesFirework = variant.startsWith("firework-");
        String callbackVariant = firesFirework ? variant.substring("firework-".length()) : variant;
        Player player = mock(Player.class);
        PlayerInventory inventory = new PlayerInventory(player);
        PlayerOffhandInventory offhand = new PlayerOffhandInventory(player);
        boolean usesOffhand = variant.startsWith("offhand");
        AtomicBoolean offhandRoute = new AtomicBoolean(usesOffhand);
        ItemCrossbow original = new ItemCrossbow();
        original.setChargedItem(Item.get(firesFirework ? ItemID.FIREWORK_ROCKET : ItemID.ARROW, 0,
                variant.startsWith("multi") ? 3 : 1));
        if (usesOffhand) offhand.slots.put(0, original);
        else inventory.slots.put(1, original);
        inventory.setHeldItemIndex(1, false);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getOffhandInventory()).thenReturn(offhand);
        when(player.isOffhandItemInteraction()).thenAnswer(call -> offhandRoute.get());
        when(player.getViewers()).thenReturn(Map.of());
        when(player.isMainThreadInputEnabled()).thenReturn(inputMode);
        when(player.getEyePosition()).thenReturn(new Vector3(0, 2, 0));
        player.level = mock(Level.class);
        when(player.getLevel()).thenReturn(player.level);
        AtomicBoolean valid = new AtomicBoolean(true);
        when(player.canContinueItemUse(anyLong())).thenAnswer(call -> valid.get());
        Server server = mock(Server.class);
        PluginManager plugins = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(plugins);
        AtomicInteger launches = new AtomicInteger();
        AtomicInteger settlements = new AtomicInteger();
        doAnswer(call -> {
            Object event = call.getArgument(0);
            if (event instanceof ProjectileLaunchEvent launch) {
                int index = launches.incrementAndGet();
                if (variant.equals("launch-slot") || variant.equals("multi-second-slot") && index == 2) {
                    inventory.setHeldItemIndex(0, false);
                }
                if (variant.equals("launch-replace")) inventory.slots.put(1, original.clone());
                if (variant.equals("launch-invalidate")) valid.set(false);
                if (variant.equals("offhand-route")) offhandRoute.set(false);
                if (variant.equals("cancel") || variant.equals("multi-second-cancel") && index == 2) {
                    launch.setCancelled();
                }
            } else if (event instanceof EntityInventoryChangeEvent change) {
                settlements.incrementAndGet();
                if (callbackVariant.equals("write-slot")) inventory.setHeldItemIndex(0, false);
                if (callbackVariant.equals("write-replace")) inventory.slots.put(1, original.clone());
                if (callbackVariant.equals("write-cancel")) change.setCancelled();
                if (callbackVariant.equals("write-modify")) change.setNewItem(original.clone());
                if (callbackVariant.equals("write-invalidate")) valid.set(false);
            }
            return null;
        }).when(plugins).callEvent(any());
        try (MockedStatic<Server> servers = mockStatic(Server.class);
             MockedConstruction<EntityArrow> arrows = mockConstruction(EntityArrow.class, (arrow, context) -> {
                 doAnswer(call -> {
                     if (inputMode) {
                         assertEquals(1, settlements.get());
                         assertTrue(((ItemCrossbow) (usesOffhand ? offhand.getItem(0) : inventory.getItem(1)))
                                 .getChargedItem().isNull());
                     }
                     if (variant.equals("multi-publish-invalidate")) valid.set(false);
                     return null;
                 }).when(arrow).spawnToAll();
             });
             MockedConstruction<EntityFirework> fireworks = mockConstruction(EntityFirework.class, (firework, context) -> {
                 doAnswer(call -> {
                     assertEquals(1, settlements.get());
                     assertTrue(((ItemCrossbow) inventory.getItem(1)).getChargedItem().isNull());
                     return null;
                 }).when(firework).spawnToAll();
             })) {
            servers.when(Server::getInstance).thenReturn(server);
            assertFalse(((ItemCrossbow) original.clone()).onClickAir(player, Vector3.ZERO));
            int actualPublished = 0;
            List<Entity> projectiles = new ArrayList<>(arrows.constructed());
            projectiles.addAll(fireworks.constructed());
            for (Entity projectile : projectiles) {
                int count = mockingDetails(projectile).getInvocations().stream()
                        .filter(call -> call.getMethod().getName().equals("spawnToAll")).toList().size();
                actualPublished += count;
                if (count == 0) verify(projectile, atLeastOnce()).close();
                else verify(projectile, never()).close();
            }
            assertEquals(published, actualPublished);
            ItemCrossbow remaining = (ItemCrossbow) (usesOffhand ? offhand.getItem(0) : inventory.getItem(1));
            assertEquals(charged, !remaining.getChargedItem().isNull());
            assertEquals(duplicate ? ItemID.CROSSBOW : ItemID.AIR, inventory.getItem(0).getId());
            if (duplicate) assertTrue(((ItemCrossbow) inventory.getItem(0)).getChargedItem().isNull());
            assertTrue(settlements.get() <= 1);
        }
    }
}
