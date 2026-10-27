package cn.nukkit;

import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.data.ServerConfiguration;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.observability.ConnectionObservation;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionCloseObservationTest {
    @BeforeAll static void initializeItems() { Block.init(); Item.init(); Attribute.init(); }

    @Test void directCloseCarriesItsActionAndReasonToTransportClosure() {
        try (Fixture fixture = new Fixture()) {
            fixture.player.close("", "disconnectionScreen.worldCorruption");
            assertEquals(List.of("disconnect_requested", "backend_player_closed", "transport_closed"), fixture.types);
            Map<String, Object> backend = fixture.events.get(1);
            Map<String, Object> transport = fixture.events.get(2);
            assertEquals(backend.get("action_id"), transport.get("action_id"));
            assertEquals("disconnectionScreen.worldCorruption", transport.get("reason_code"));
            assertEquals("server", transport.get("close_origin"));
            assertFalse(fixture.player.isConnected());
            verify(fixture.source).close(fixture.player, "disconnectionScreen.worldCorruption");
        }
    }

    @Test void transportTimeoutDoesNotInventServerAction() {
        try (Fixture fixture = new Fixture()) {
            fixture.player.observeTransportClosed("TIMED_OUT");
            assertEquals(Map.of("transport_reason", "TIMED_OUT"), fixture.events.getFirst());
        }
    }

    @Test void cancelledKickDoesNotLeakAnActionIntoLaterTimeout() {
        try (Fixture fixture = new Fixture()) {
            doAnswer(call -> {
                PlayerKickEvent event = call.getArgument(0);
                event.setCancelled();
                return null;
            }).when(fixture.plugins).callEvent(any(PlayerKickEvent.class));
            assertFalse(fixture.player.kickObserved("private reason", "test_kick", "policy_action"));
            fixture.player.observeTransportClosed("TIMED_OUT");
            assertEquals(List.of("disconnect_requested", "disconnect_request_cancelled", "transport_closed"), fixture.types);
            assertEquals(Map.of("transport_reason", "TIMED_OUT"), fixture.events.getLast());
            verify(fixture.source, never()).close(any(), anyString());
        }
    }

    @Test void onlyFixedReasonsArePreserved() {
        assertEquals("malformed_packet", ConnectionObservation.reasonCode("Sent malformed packet"));
        assertEquals("unclassified_server_close", ConnectionObservation.reasonCode("token=private-value 127.0.0.1"));
        assertEquals("unclassified_server_close", ConnectionObservation.reasonCode("disconnect.reason\nprivate"));
        assertEquals("unclassified_server_close", ConnectionObservation.reasonCode(null));
    }

    private static final class Fixture implements AutoCloseable {
        final SourceInterface source = mock(SourceInterface.class);
        final PluginManager plugins = mock(PluginManager.class);
        final List<String> types = new ArrayList<>();
        final List<Map<String, Object>> events = new ArrayList<>();
        final MockedStatic<Server> servers = mockStatic(Server.class);
        final Probe player;
        Fixture() {
            Server server = mock(Server.class);
            when(server.getPluginManager()).thenReturn(plugins);
            when(server.getConfiguration()).thenReturn(mock(ServerConfiguration.class));
            when(server.getOnlinePlayerList()).thenReturn(new Player[0]);
            when(server.getLanguage()).thenReturn(mock(BaseLang.class));
            servers.when(Server::getInstance).thenReturn(server);
            try { player = new Probe(source); }
            catch (RuntimeException | Error exception) { servers.close(); throw exception; }
            doAnswer(call -> { player.observeTransportClosed("DISCONNECTED"); return null; }).when(source).close(any(), anyString());
            ConnectionObservation.setObserver((observed, type, data) -> { types.add(type); events.add(data); });
        }
        @Override public void close() { ConnectionObservation.setObserver(null); servers.close(); }
    }

    private static class Probe extends Player {
        Probe(SourceInterface source) { super(source, 1L, new InetSocketAddress("127.0.0.1", 19132)); }
        // 使用真实关闭实现，只替换网络发包与窗口清理。
        @Override public void sendDisconnectScreen(String reason) { }
        @Override public void removeAllWindows(boolean permanent) { }
    }
}
