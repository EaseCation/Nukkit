package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LevelChangeAcceptanceTest {
    @Test void cancellationNeverPreparesWorldRemoval() { check(true); }
    @Test void acceptedHookRunsAfterEventAndBeforeRemoval() { check(false); }

    private void check(boolean cancelled) {
        Entity entity = mock(Entity.class, CALLS_REAL_METHODS);
        Server server = mock(Server.class);
        PluginManager plugins = mock(PluginManager.class);
        Level origin = mock(Level.class);
        Level target = mock(Level.class);
        entity.server = server;
        entity.level = origin;
        when(server.getPluginManager()).thenReturn(plugins);
        doNothing().when(entity).despawnFromAll();
        List<String> order = new ArrayList<>();
        doAnswer(call -> {
            order.add("event");
            ((EntityLevelChangeEvent) call.getArgument(0)).setCancelled(cancelled);
            return null;
        }).when(plugins).callEvent(any(EntityLevelChangeEvent.class));
        doAnswer(call -> {
            order.add("accepted");
            assertSame(origin, entity.getLevel());
            return null;
        }).when(entity).onLevelChangeAccepted(target);
        doAnswer(call -> { order.add("remove"); return null; }).when(origin).removeEntity(entity);
        assertEquals(!cancelled, entity.switchLevel(target));
        assertEquals(cancelled ? List.of("event") : List.of("event", "accepted", "remove"), order);
        assertSame(cancelled ? origin : target, entity.getLevel());
        verify(target, cancelled ? never() : times(1)).addEntity(entity);
    }
}
