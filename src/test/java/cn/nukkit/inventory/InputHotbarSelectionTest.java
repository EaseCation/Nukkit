package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InputHotbarSelectionTest {
    private Player player;
    private PlayerInventory inventory;
    private PluginManager plugins;

    @BeforeEach
    void prepare() {
        player = mock(Player.class);
        inventory = mock(PlayerInventory.class, CALLS_REAL_METHODS);
        plugins = mock(PluginManager.class);
        Level level = mock(Level.class);
        Server server = mock(Server.class);
        doReturn(player).when(inventory).getHolder();
        doReturn(new Item(ItemID.SNOWBALL, 0, 8)).when(inventory).getItem(anyInt());
        doReturn(Map.of()).when(player).getViewers();
        doReturn(level).when(player).getLevel();
        when(level.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(plugins);
        when(player.isMainThreadInputEnabled()).thenReturn(true);
        when(player.isOnline()).thenReturn(true);
        when(player.isAlive()).thenReturn(true);
        when(player.getUsingItemHand()).thenReturn(ItemUseHand.MAIN_HAND);
    }

    @Test
    void changedSlotStopsMainHandOnceAndSameSlotDoesNotCallEventAgain() {
        assertTrue(inventory.equipItem(1));
        assertEquals(1, inventory.getHeldItemIndex());
        assertTrue(inventory.equipItem(1));
        verify(plugins, times(1)).callEvent(any(PlayerItemHeldEvent.class));
        verify(player, times(1)).setUsingItem(false);
    }

    @Test
    void changedMainSlotPreservesActiveOffhandUse() {
        when(player.getUsingItemHand()).thenReturn(ItemUseHand.OFF_HAND);
        assertTrue(inventory.equipItem(1));
        verify(player, never()).setUsingItem(false);
    }

    @Test
    void teleportInEventCannotCommitAnOldSlotChange() {
        doAnswer(call -> {
            when(player.getMovementEpoch()).thenReturn(1L);
            return null;
        }).when(plugins).callEvent(any(PlayerItemHeldEvent.class));
        assertFalse(inventory.equipItem(1));
        assertEquals(0, inventory.getHeldItemIndex());
        verify(player, never()).setUsingItem(false);
    }

    @Test
    void eventSelectingAnotherSlotWinsOverOuterSelection() {
        doAnswer(call -> {
            inventory.setHeldItemIndex(2, false);
            return null;
        }).when(plugins).callEvent(any(PlayerItemHeldEvent.class));
        assertFalse(inventory.equipItem(1));
        assertEquals(2, inventory.getHeldItemIndex());
    }

    @Test
    void disabledModePreservesRepeatedSelectionEventAndUseState() {
        when(player.isMainThreadInputEnabled()).thenReturn(false);
        assertTrue(inventory.equipItem(0));
        assertTrue(inventory.equipItem(0));
        verify(plugins, times(2)).callEvent(any(PlayerItemHeldEvent.class));
        verify(player, never()).setUsingItem(false);
    }
}
