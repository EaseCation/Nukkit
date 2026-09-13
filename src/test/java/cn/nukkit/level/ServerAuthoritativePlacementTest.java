package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerAuthoritativePlacementTest {
    private final Level level = mock(Level.class);
    private final Player player = mock(Player.class);
    private final PluginManager plugins = mock(PluginManager.class);
    private final Item item = mock(Item.class);
    private final Block clicked = mock(Block.class);
    private final Block replaced = mock(Block.class);
    private final Block placed = mock(Block.class);
    private final Vector3 position = new Vector3(2, 10, 2);
    private boolean authoritative;
    private boolean cancelPlace;

    @BeforeEach
    void configure() {
        Server server = mock(Server.class);
        when(level.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(plugins);
        when(level.getHeightRange()).thenReturn(HeightRange.MINIMUM);
        when(level.getBlock(position)).thenReturn(clicked);
        when(clicked.getSide(BlockFace.UP)).thenReturn(replaced);
        when(replaced.canBeReplaced()).thenReturn(true);
        when(replaced.getId()).thenReturn(Block.AIR);
        when(replaced.blockCenter()).thenReturn(new Vector3(2.5, 11.5, 2.5));
        when(placed.getId()).thenReturn(Block.STONE);
        when(level.getExtraBlock(replaced)).thenReturn(mock(Block.class));
        when(item.canBePlaced()).thenReturn(true);
        when(item.getBlock()).thenReturn(placed);
        when(item.getCount()).thenReturn(2);
        when(placed.getPlacementBlock(item, replaced, clicked, BlockFace.UP, 0, 0, 0, player)).thenReturn(placed);
        when(placed.canPassThrough()).thenReturn(true);
        when(placed.place(item, replaced, clicked, BlockFace.UP, 0, 0, 0, player)).thenReturn(true);
        when(player.canPlaceOn(clicked, item)).thenReturn(true);
        when(player.getViewers()).thenReturn(Map.of());
        doAnswer(call -> {
            Object event = call.getArgument(0);
            if (event instanceof PlayerInteractEvent interact) interact.setBlockPlacementServerAuthoritative(authoritative);
            if (event instanceof BlockPlaceEvent place && cancelPlace) place.setCancelled();
            return null;
        }).when(plugins).callEvent(any());
        doCallRealMethod().when(level).useItemOn(position, item, BlockFace.UP, 0, 0, 0, player, false, false);
    }

    @Test
    void ordinaryPlacementStillHonorsClientFailure() {
        assertNull(place());
        verify(plugins, never()).callEvent(any(BlockPlaceEvent.class));
        verify(item, never()).setCount(anyInt());
    }

    @Test
    void authorizedPlacementCanSucceedDespiteClientFailureAndConsumesExactlyOne() {
        authoritative = true;
        assertSame(item, place());
        verify(plugins).callEvent(any(BlockPlaceEvent.class));
        verify(placed).place(item, replaced, clicked, BlockFace.UP, 0, 0, 0, player);
        verify(item).setCount(1);
    }

    @Test
    void placementCancellationStillPreventsConsumption() {
        authoritative = true;
        cancelPlace = true;
        assertNull(place());
        verify(placed, never()).place(item, replaced, clicked, BlockFace.UP, 0, 0, 0, player);
        verify(item, never()).setCount(anyInt());
    }

    @Test
    void serverPermissionStillPreventsPlacement() {
        authoritative = true;
        when(player.canPlaceOn(clicked, item)).thenReturn(false);
        assertNull(place());
        verify(plugins, never()).callEvent(any(BlockPlaceEvent.class));
        verify(item, never()).setCount(anyInt());
    }

    private Item place() {
        return level.useItemOn(position, item, BlockFace.UP, 0, 0, 0, player, false, false);
    }
}
