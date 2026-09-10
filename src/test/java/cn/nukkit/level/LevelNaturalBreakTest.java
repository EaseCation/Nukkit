package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockNaturalBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LevelNaturalBreakTest {
    private final Level level = mock(Level.class);
    private final Block block = mock(Block.class);
    private final BlockEntity blockEntity = mock(BlockEntity.class);
    private final Item item = mock(Item.class);
    private final Item drop = mock(Item.class);
    private final Vector3 position = new Vector3(1, 2, 3);
    private final BreakListener listener = new BreakListener();

    @BeforeEach
    void setUp() {
        Server server = mock(Server.class);
        PluginManager plugins = new PluginManager(null, null);
        when(level.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(plugins);
        Plugin plugin = mock(Plugin.class);
        when(plugin.isEnabled()).thenReturn(true);
        when(plugin.getMethodHandlesLookup()).thenReturn(MethodHandles.lookup());
        plugins.registerEvents(listener, plugin);

        when(level.getBlock(position)).thenReturn(block);
        when(level.getBlock(block)).thenReturn(block);
        when(level.getExtraBlock(block)).thenReturn(mock(Block.class));
        when(level.getBlockEntity(block)).thenReturn(blockEntity);
        when(block.isBreakable(item)).thenReturn(true);
        when(block.getDrops(item)).thenReturn(new Item[]{drop});
        when(block.getCreativeDrops()).thenReturn(new Item[]{drop});
        when(drop.getCount()).thenReturn(1);
        GameRules rules = mock(GameRules.class);
        when(rules.getBoolean(GameRule.DO_TILE_DROPS)).thenReturn(true);
        when(level.getGameRules()).thenReturn(rules);
        doCallRealMethod().when(level).useBreakOn(position, null, item, null, true);
    }

    @AfterEach
    void tearDown() {
        HandlerList.unregisterAll(listener);
    }

    @Test
    void cancellationPreventsAllBreakSideEffects() {
        listener.cancelNatural = true;
        assertNull(level.useBreakOn(position, null, item, null, true));
        assertEquals(1, listener.naturalEvents);
        assertEquals(0, listener.playerEvents);
        verify(block, never()).onBreak(any(Item.class), nullable(Player.class));
        verify(blockEntity, never()).onBreak();
        verify(blockEntity, never()).close();
        verify(level, never()).addParticle(any(Particle.class));
        verify(level, never()).dropItem(any(Vector3.class), any(Item.class));
        verify(item, never()).useOn(block);
    }

    @Test
    void allowedNaturalBreakRetainsEntitiesParticlesAndDrops() {
        assertSame(item, level.useBreakOn(position, null, item, null, true));
        assertEquals(1, listener.naturalEvents);
        assertEquals(0, listener.playerEvents);
        verify(block).onBreak(item, null);
        verify(blockEntity).onBreak();
        verify(blockEntity).close();
        verify(level).addParticle(any(Particle.class));
        verify(level).dropItem(position.add(0.5, 0.5, 0.5), drop);
        verify(item).useOn(block);
    }

    @Test
    void playerBreakUsesOnlyPlayerEvent() {
        listener.cancelNatural = true;
        Player player = creativePlayer();
        assertSame(item, level.useBreakOn(position, null, item, player, true));
        assertEquals(0, listener.naturalEvents);
        assertEquals(1, listener.playerEvents);
        verify(block).onBreak(item, player);
    }

    @Test
    void playerBreakCancellationStillPreventsDestruction() {
        listener.cancelPlayer = true;
        Player player = creativePlayer();
        assertNull(level.useBreakOn(position, null, item, player, true));
        assertEquals(0, listener.naturalEvents);
        assertEquals(1, listener.playerEvents);
        verify(block, never()).onBreak(item, player);
        verify(blockEntity, never()).close();
    }

    private Player creativePlayer() {
        Player player = mock(Player.class);
        when(player.isCreative()).thenReturn(true);
        when(player.canDestroy(block, item)).thenReturn(true);
        doCallRealMethod().when(level).useBreakOn(position, null, item, player, true);
        return player;
    }

    private static class BreakListener implements Listener {
        private boolean cancelNatural;
        private boolean cancelPlayer;
        private int naturalEvents;
        private int playerEvents;

        @EventHandler
        public void onNaturalBreak(BlockNaturalBreakEvent event) {
            naturalEvents++;
            if (cancelNatural) {
                event.setCancelled();
            }
        }

        @EventHandler
        public void onPlayerBreak(BlockBreakEvent event) {
            playerEvents++;
            if (cancelPlayer) {
                event.setCancelled();
            }
        }
    }
}
