package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.inventory.ItemUseHand;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemShield;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemUseCompletionTest {
    @BeforeAll static void initialize() { Block.init(); Item.init(); }

    @Test void normalCompletionCanReplaceItsOwnStackAndContinue() { complete("normal", true, true); }
    @Test void rejectedUseResyncsAndKeepsExistingRetrySemantics() { complete("reject", true, true); }
    @Test void slotChangeDuringUseCannotRestartOldHand() { complete("use-slot", true, false); }
    @Test void invalidatedUseCannotRestart() { complete("use-invalidate", true, false); }
    @Test void removalDuringUseCannotRestart() { complete("use-remove", true, false); }
    @Test void changedItemTypeCannotRestartOldUse() { complete("use-type", true, false); }
    @Test void sameTypeRebindingDoesNotLockTheWholeUseLifetime() { complete("use-same-type", true, true); }
    @Test void slotChangeDuringStopPreventsUse() { complete("stop-slot", true, false); }
    @Test void equalReplacementDuringStopPreventsUse() { complete("stop-replace", true, false); }
    @Test void newUseStartedDuringStopKeepsItsOwnStart() { complete("stop-new-use", true, true); }
    @Test void newUseStartedInCallbackKeepsItsOwnStart() { complete("use-new-use", true, true); }
    @Test void mainSelectionChangePreservesOffhandCompletion() { complete("offhand-slot", true, true); }
    @Test void changedOffhandRouteCannotRestart() { complete("offhand-route", true, false); }
    @Test void disabledModeRetainsOldRestartAfterSlotChange() { complete("use-slot", false, true); }

    @Test void explicitShieldPathRetainsContinuationEligibility() { shield(true); }
    @Test void ordinaryNonReleasableItemDoesNotRestart() { shield(false); }
    @Test void completedItemCanEndItsContinuousUse() { completed(false, true); }
    @Test void disabledModeDoesNotCallTheNewContinuationPolicy() { completed(true, false); }

    private void completed(boolean using, boolean enabled) {
        try (Fixture fixture = new Fixture(enabled, false)) {
            fixture.continuation = false;
            fixture.player.completeItemUse(new CallbackItem(fixture), 25, false);
            assertEquals(using, fixture.using.get());
            assertEquals(1, fixture.uses.get());
        }
    }

    private void shield(boolean explicit) {
        try (Fixture fixture = new Fixture(true, false)) {
            ItemShield shield = new ItemShield();
            fixture.inventory.setItem(1, shield);
            fixture.player.completeItemUse(shield, 25, explicit);
            assertEquals(explicit, fixture.using.get());
        }
    }

    @Test void normalPeriodicUseCompletesOnce() { periodic("normal", true, 1, 1, true); }
    @Test void periodicOnUsingSlotChangeStopsBeforeAirUse() { periodic("using-slot", true, 0, 0, false); }
    @Test void periodicInteractionSlotChangeCannotCopyTheItem() { periodic("interact-slot", true, 0, 0, false); }
    @Test void periodicAirCallbackSelectionChangeStopsBeforeWrite() { periodic("air-slot", true, 1, 0, false); }
    @Test void periodicSameValueReplacementWins() { periodic("interact-replace", true, 0, 0, false); }
    @Test void periodicNewUseStartedDuringOnUsingIsPreserved() { periodic("using-new-use", true, 0, 0, true); }
    @Test void periodicCancelledInteractionStopsBeforeAirUse() { periodic("interact-cancel", true, 0, 0, true); }
    @Test void disabledPeriodicModeRetainsOriginalCurrentHandWrite() { periodic("interact-slot", false, 1, 1, true); }

    private void complete(String variant, boolean enabled, boolean using) {
        try (Fixture fixture = new Fixture(enabled, variant.startsWith("offhand"))) {
            fixture.stopAction = () -> {
                if (variant.equals("stop-slot")) fixture.inventory.setHeldItemIndex(0, false);
                if (variant.equals("stop-replace")) fixture.inventory.setItem(1, new CallbackItem(fixture));
                if (variant.equals("stop-new-use")) fixture.player.setUsingItem(true);
            };
            fixture.useAction = item -> {
                if (variant.equals("use-slot") || variant.equals("offhand-slot")) fixture.inventory.setHeldItemIndex(0, false);
                if (variant.equals("use-invalidate")) fixture.valid.set(false);
                if (variant.equals("use-remove")) fixture.inventory.clear(1);
                if (variant.equals("use-type")) fixture.inventory.setItem(1, new Item(ItemID.SNOWBALL));
                if (variant.equals("use-same-type")) fixture.inventory.setItem(1, new CallbackItem(fixture));
                if (variant.equals("offhand-route")) fixture.offhandRoute.set(false);
                if (variant.equals("normal")) fixture.inventory.setItem(1, item);
                if (variant.equals("use-new-use")) fixture.player.setUsingItem(true);
            };
            fixture.accepted = !variant.equals("reject");
            fixture.player.completeItemUse(new CallbackItem(fixture), 25, false);
            assertEquals(using, fixture.using.get());
            assertEquals(variant.startsWith("stop-") ? 0 : 1, fixture.uses.get());
            if (variant.equals("stop-new-use") || variant.equals("use-new-use")) {
                assertEquals(30, fixture.player.startAction);
                verify(fixture.player, times(1)).setUsingItem(true);
            }
            if (variant.equals("reject")) verify(fixture.inventory).sendContents(fixture.player);
        }
    }

    private void periodic(String variant, boolean enabled, int clicks, int uses, boolean using) {
        try (Fixture fixture = new Fixture(enabled, false)) {
            fixture.usingAction = () -> {
                if (variant.equals("using-slot")) fixture.inventory.setHeldItemIndex(0, false);
                if (variant.equals("using-new-use")) fixture.player.setUsingItem(true);
            };
            fixture.airAction = () -> {
                if (variant.equals("air-slot")) fixture.inventory.setHeldItemIndex(0, false);
            };
            doAnswer(call -> {
                if (call.getArgument(0) instanceof PlayerInteractEvent event) {
                    if (variant.equals("interact-slot")) fixture.inventory.setHeldItemIndex(0, false);
                    if (variant.equals("interact-replace")) fixture.inventory.setItem(1, new CallbackItem(fixture));
                    if (variant.equals("interact-cancel")) event.setCancelled();
                }
                return null;
            }).when(fixture.plugins).callEvent(any());
            fixture.player.tickActiveItemUse();
            assertEquals(clicks, fixture.clicks.get());
            assertEquals(uses, fixture.uses.get());
            assertEquals(using, fixture.using.get());
            assertEquals(variant.equals("interact-slot") && !enabled ? ItemID.BOW : ItemID.AIR,
                    fixture.inventory.getItem(0).getId());
            if (variant.equals("using-new-use")) assertEquals(30, fixture.player.startAction);
        }
    }

    private static class Fixture implements AutoCloseable {
        final ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        final PluginManager plugins = mock(PluginManager.class);
        final PlayerInventory inventory = spy(new PlayerInventory(player));
        final PlayerOffhandInventory offhand = spy(new PlayerOffhandInventory(player));
        final AtomicBoolean using = new AtomicBoolean(true);
        final AtomicBoolean valid = new AtomicBoolean(true);
        final AtomicBoolean offhandRoute = new AtomicBoolean();
        final AtomicInteger uses = new AtomicInteger();
        final AtomicInteger clicks = new AtomicInteger();
        final MockedStatic<Server> servers;
        Runnable stopAction = () -> { };
        Runnable usingAction = () -> { };
        Runnable airAction = () -> { };
        Consumer<Item> useAction = item -> { };
        boolean accepted = true;
        boolean continuation = true;

        Fixture(boolean enabled, boolean usesOffhand) {
            Server server = mock(Server.class);
            when(server.getTick()).thenReturn(30);
            when(server.getPluginManager()).thenReturn(plugins);
            servers = mockStatic(Server.class);
            servers.when(Server::getInstance).thenReturn(server);
            player.configure(server, inventory, offhand);
            offhandRoute.set(usesOffhand);
            doReturn(enabled).when(player).isMainThreadInputEnabled();
            doAnswer(call -> valid.get()).when(player).canContinueItemUse(anyLong());
            doAnswer(call -> offhandRoute.get()).when(player).isOffhandItemInteraction();
            doReturn(usesOffhand).when(player).isUsingOffhandItem();
            doReturn(true).when(player).isUsingSameItem(any());
            doReturn(ItemUseHand.MAIN_HAND).when(player).setItemInteractionHand(any());
            doReturn(true).when(player).isSurvivalLike();
            doReturn(false).when(player).isSpectator();
            doReturn(Map.of()).when(player).getViewers();
            doReturn(ContainerIds.INVENTORY).when(player).getWindowId(any());
            doReturn(true).when(player).dataPacket(any());
            // 测试不覆盖触控提示文字；与包发送一样替代这个 UI 发布边界。
            doNothing().when(player).setButtonText(anyString());
            doReturn(Vector3.ZERO).when(player).getDirectionVector();
            doNothing().when(inventory).sendContents(player);
            doNothing().when(offhand).sendContents(player);
            doAnswer(call -> using.get()).when(player).isUsingItem();
            doAnswer(call -> {
                boolean value = call.getArgument(0);
                using.set(value);
                player.startAction = value ? 30 : -1;
                player.startActionTimestamp = value ? System.currentTimeMillis() : -1;
                if (!value) {
                    Runnable action = stopAction;
                    stopAction = () -> { };
                    action.run();
                }
                return null;
            }).when(player).setUsingItem(anyBoolean());
            if (usesOffhand) offhand.setItem(0, new CallbackItem(this));
            else inventory.setItem(1, new CallbackItem(this));
            inventory.setHeldItemIndex(1, false);
            clearInvocations(player, inventory, offhand);
        }

        @Override public void close() { servers.close(); }
    }

    static class CallbackItem extends ItemBow {
        private final Fixture fixture;
        CallbackItem(Fixture fixture) { this.fixture = fixture; }
        @Override public int getUseDuration() { return 25; }
        @Override public boolean canContinueUsing() { return fixture.continuation; }
        @Override public void onUsing(Player player, int ticksUsed) { fixture.usingAction.run(); }
        @Override public boolean onClickAir(Player player, Vector3 direction) {
            fixture.clicks.incrementAndGet();
            fixture.airAction.run();
            return true;
        }
        @Override public boolean onUse(Player player, int ticksUsed) {
            fixture.uses.incrementAndGet();
            fixture.useAction.accept(this);
            return fixture.accepted;
        }
    }

    static class ProbePlayer extends Player {
        ProbePlayer() { super(null, 0L, new InetSocketAddress("127.0.0.1", 19132)); }
        void configure(Server server, PlayerInventory inventory, PlayerOffhandInventory offhand) {
            this.server = server;
            this.inventory = inventory;
            this.offhandInventory = offhand;
            this.startAction = 0;
            this.startActionTimestamp = System.currentTimeMillis() - 1400;
        }
    }
}
