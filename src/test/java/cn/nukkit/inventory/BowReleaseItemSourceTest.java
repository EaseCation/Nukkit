package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.bow.EnchantmentBowInfinity;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.potion.PotionID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BowReleaseItemSourceTest {
    @BeforeAll static void initialize() { Block.init(); Item.init(); Effect.init(); Potion.init(); }

    @Test void normalReleaseConsumesOneArrowAndDamagesOriginalBow() { check("normal", true, true, 7, 1, false); }
    @Test void earlySelectionChangeCannotFireOrDuplicateBow() { check("early-slot", true, false, 8, 0, false); }
    @Test void disabledModeRetainsLegacySelectedSlotWrite() { check("early-slot", false, true, 7, 0, true); }
    @Test void equalValueReplacementBeforeConsumptionStopsRelease() { check("early-replace", true, false, 8, 0, false); }
    @Test void selectionChangeDuringArrowConsumptionWritesOriginalBow() { check("late-slot", true, true, 7, 1, false); }
    @Test void replacementDuringDurabilityEventWins() { check("write-replace", true, true, 7, 0, false); }
    @Test void invalidationDuringArrowConsumptionPreventsLaunch() { check("late-invalidate", true, false, 7, 0, false); }
    @Test void invalidationDuringLaunchEventClosesProjectile() { check("launch-invalidate", true, false, 7, 1, false); }
    @Test void cancelledShotRetainsBothItems() { check("cancel", true, false, 8, 0, false); }
    @Test void offhandBowKeepsOriginalHandAfterArrowCallback() { check("offhand-late-slot", true, true, 7, 1, false); }
    @Test void removedAmmoCannotProduceAProjectile() { check("ammo-remove", true, false, 0, 0, false); }
    @Test void equalValueAmmoReplacementInvalidatesTheOldSource() { check("ammo-replace", true, false, 8, 0, false); }
    @Test void movedAmmoIsNotResearchedInAnotherSlot() { check("ammo-move", true, false, 0, 0, false); }
    @Test void cancelledConsumptionCannotProduceAProjectile() { check("ammo-cancel", true, false, 8, 0, false); }
    @Test void modifiedConsumptionResultCannotProduceAProjectile() { check("ammo-modify", true, false, 8, 0, false); }
    @Test void nestedAmmoReplacementWinsOverConsumption() { check("ammo-write-replace", true, false, 8, 0, false); }
    @Test void lastArrowCanBeConsumed() { check("ammo-last", true, true, 0, 1, false); }
    @Test void offhandAmmoStillHasPriority() { check("ammo-offhand", true, true, 8, 1, false); }
    @Test void infinityRetainsItsRequiredArrow() { check("infinity", true, true, 8, 1, false); }
    @Test void infinityStillConsumesTippedArrows() { check("infinity-tipped", true, true, 7, 1, false); }
    @Test void creativeCanReleaseWithoutAmmo() { check("creative-empty", true, true, 0, 0, false); }
    @Test void disabledModeRetainsLegacyCancelledConsumption() { check("ammo-cancel", false, true, 8, 1, false); }

    private void check(String variant, boolean inputMode, boolean launched, int arrowCount, int bowDamage, boolean duplicate) {
        Player player = mock(Player.class);
        PlayerInventory inventory = new PlayerInventory(player);
        PlayerOffhandInventory offhand = new PlayerOffhandInventory(player);
        boolean offhandUse = variant.startsWith("offhand");
        ItemBow original = new ItemBow();
        if (variant.startsWith("infinity")) original.addEnchantment(new EnchantmentBowInfinity().setLevel(1));
        if (offhandUse) offhand.slots.put(0, original);
        else inventory.slots.put(1, original);
        inventory.slots.put(2, Item.get(ItemID.ARROW, 0, 8));
        if (variant.equals("ammo-last")) inventory.slots.get(2).setCount(1);
        if (variant.equals("infinity-tipped")) inventory.slots.get(2).setDamage(ItemArrow.TIPPED_ARROW + PotionID.POISON);
        if (variant.equals("ammo-offhand")) offhand.slots.put(0, Item.get(ItemID.ARROW, 0, 8));
        if (variant.equals("creative-empty")) inventory.slots.remove(2);
        inventory.setHeldItemIndex(1, false);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getOffhandInventory()).thenReturn(offhand);
        when(player.isOffhandItemInteraction()).thenReturn(offhandUse);
        when(player.getViewers()).thenReturn(Map.of());
        when(player.isMainThreadInputEnabled()).thenReturn(inputMode);
        when(player.isSurvivalLike()).thenReturn(!variant.equals("creative-empty"));
        when(player.isCreative()).thenReturn(variant.equals("creative-empty"));
        when(player.getEyePosition()).thenReturn(new Vector3(0, 2, 0));
        player.level = mock(Level.class);
        when(player.getLevel()).thenReturn(player.level);
        AtomicBoolean valid = new AtomicBoolean(true);
        when(player.canContinueItemUse(anyLong())).thenAnswer(call -> valid.get());
        Server server = mock(Server.class);
        PluginManager plugins = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(plugins);
        AtomicBoolean replaced = new AtomicBoolean();
        doAnswer(call -> {
            Object event = call.getArgument(0);
            if (event instanceof EntityShootBowEvent shot) {
                if (variant.equals("early-slot")) inventory.setHeldItemIndex(0, false);
                if (variant.equals("early-replace")) inventory.slots.put(1, original.clone());
                if (variant.equals("cancel")) shot.setCancelled();
                if (variant.equals("ammo-remove") || variant.equals("ammo-move")) inventory.slots.remove(2);
                if (variant.equals("ammo-move")) inventory.slots.put(3, Item.get(ItemID.ARROW, 0, 8));
                if (variant.equals("ammo-replace")) inventory.slots.put(2, inventory.slots.get(2).clone());
            } else if (event instanceof EntityInventoryChangeEvent change) {
                if (change.getSlot() == 2) {
                    if (variant.equals("late-slot") || offhandUse) inventory.setHeldItemIndex(0, false);
                    if (variant.equals("late-invalidate")) valid.set(false);
                    if (variant.equals("ammo-cancel")) change.setCancelled();
                    if (variant.equals("ammo-modify")) change.getNewItem().setCount(8);
                    if (variant.equals("ammo-write-replace")) inventory.slots.put(2, inventory.slots.get(2).clone());
                } else if (change.getSlot() == 1 && variant.equals("write-replace") && !replaced.getAndSet(true)) {
                    inventory.slots.put(1, original.clone());
                }
            } else if (event instanceof ProjectileLaunchEvent && variant.equals("launch-invalidate")) valid.set(false);
            return null;
        }).when(plugins).callEvent(any());
        try (MockedStatic<Server> servers = mockStatic(Server.class);
             MockedConstruction<EntityArrow> arrows = mockConstruction(EntityArrow.class, (arrow, context) -> {
                 when(arrow.getMotion()).thenReturn(new Vector3(0, 0, 1));
             })) {
            servers.when(Server::getInstance).thenReturn(server);
            ItemBow bow = (ItemBow) original.clone();
            bow.onRelease(player, 20, Vector3.ZERO);
            assertEquals(1, arrows.constructed().size());
            EntityArrow projectile = arrows.constructed().getFirst();
            verify(projectile, launched ? times(1) : never()).spawnToAll();
            if (!launched) verify(projectile).close();
            assertEquals(arrowCount, inventory.getItem(2).count);
            assertEquals(bowDamage, (offhandUse ? offhand.getItem(0) : inventory.getItem(1)).getDamage());
            assertEquals(duplicate ? ItemID.BOW : ItemID.AIR, inventory.getItem(0).getId());
            if (duplicate) assertEquals(1, inventory.getItem(0).getDamage());
            if (variant.equals("ammo-move")) assertEquals(8, inventory.getItem(3).count);
            if (variant.equals("ammo-offhand")) assertEquals(7, offhand.getItem(0).count);
        }
    }
}
