package cn.nukkit;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.InetSocketAddress;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatPublicationLifecycleTest {
    @Test
    void firstEventCommittedTransferStopsCurrentPublicationAndLaterLine() {
        try (Fixture fixture = new Fixture(true)) {
            fixture.onChat(() -> doReturn(false).when(fixture.player).isAcceptingInputPackets());
            assertFalse(fixture.player.chat("first\nsecond"));
            verify(fixture.plugins, times(1)).callEvent(any(PlayerChatEvent.class));
            verify(fixture.server, never()).broadcastMessage(anyString(), anyCollection());
        }
    }

    @Test
    void alreadyRetiredDirectChatDoesNotInvokePluginEvents() {
        try (Fixture fixture = new Fixture(true)) {
            doReturn(false).when(fixture.player).isAcceptingInputPackets();
            assertFalse(fixture.player.chat("old"));
            verify(fixture.plugins, never()).callEvent(any(PlayerChatEvent.class));
        }
    }

    @Test
    void closingDuringEventStopsCurrentMessage() {
        try (Fixture fixture = new Fixture(true)) {
            fixture.onChat(() -> {
                fixture.player.spawned = false;
                doReturn(false).when(fixture.player).isAcceptingInputPackets();
            });
            assertFalse(fixture.player.chat("closing"));
            verify(fixture.server, never()).broadcastMessage(anyString(), anyCollection());
        }
    }

    @Test
    void retirementDuringCommittedPublicationPreservesItAndStopsNextLineEvent() {
        try (Fixture fixture = new Fixture(true)) {
            doAnswer(call -> {
                doReturn(false).when(fixture.player).isAcceptingInputPackets();
                return 1;
            }).when(fixture.server).broadcastMessage(anyString(), anyCollection());
            assertFalse(fixture.player.chat("first\nsecond"));
            verify(fixture.plugins, times(1)).callEvent(any(PlayerChatEvent.class));
            verify(fixture.server, times(1)).broadcastMessage(anyString(), anyCollection());
        }
    }

    @Test
    void legalWorldChangePreservesTwoLineChatAndExistingLimit() {
        try (Fixture fixture = new Fixture(true)) {
            fixture.onChat(() -> doReturn(2L).when(fixture.player).getMovementEpoch());
            assertTrue(fixture.player.chat("first\nsecond"));
            verify(fixture.plugins, times(2)).callEvent(any(PlayerChatEvent.class));
            verify(fixture.server, times(2)).broadcastMessage(anyString(), anyCollection());
            assertEquals(0, fixture.player.counter());
            verify(fixture.player, never()).getMovementEpoch();
        }
    }

    @Test
    void pluginCancellationStillConsumesQuotaWithoutPublishing() {
        try (Fixture fixture = new Fixture(true)) {
            doAnswer(call -> {
                PlayerChatEvent event = call.getArgument(0);
                event.setCancelled();
                return null;
            }).when(fixture.plugins).callEvent(any(PlayerChatEvent.class));
            assertTrue(fixture.player.chat("first\nsecond"));
            assertEquals(0, fixture.player.counter());
            verify(fixture.plugins, times(2)).callEvent(any(PlayerChatEvent.class));
            verify(fixture.server, never()).broadcastMessage(anyString(), anyCollection());
        }
    }

    @Test
    void modifiedTextAndPluginRecipientsRemainAuthoritative() {
        try (Fixture fixture = new Fixture(true)) {
            Set<CommandSender> recipients = Set.of(mock(CommandSender.class));
            doAnswer(call -> {
                PlayerChatEvent event = call.getArgument(0);
                event.setMessage("modified");
                event.setRecipients(recipients);
                return null;
            }).when(fixture.plugins).callEvent(any(PlayerChatEvent.class));
            assertTrue(fixture.player.chat("original"));
            verify(fixture.server).broadcastMessage(eq("notification"), eq(recipients));
        }
    }

    @Test
    void disabledModePreservesSecondLineAndPublicationAfterTransferCallback() {
        try (Fixture fixture = new Fixture(false)) {
            fixture.onChat(() -> doReturn(false).when(fixture.player).isAcceptingInputPackets());
            assertTrue(fixture.player.chat("first\nsecond"));
            verify(fixture.plugins, times(2)).callEvent(any(PlayerChatEvent.class));
            verify(fixture.server, times(2)).broadcastMessage(anyString(), anyCollection());
            verify(fixture.player, never()).isAcceptingInputPackets();
        }
    }

    private static final class Fixture implements AutoCloseable {
        private final Server server = mock(Server.class);
        private final PluginManager plugins = mock(PluginManager.class);
        private final ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        private final MockedStatic<Server> servers = mockStatic(Server.class);

        private Fixture(boolean inputMode) {
            when(server.getPluginManager()).thenReturn(plugins);
            when(plugins.getPermissionSubscriptions(Server.BROADCAST_CHANNEL_USERS)).thenReturn(Set.<Permissible>of());
            BaseLang language = mock(BaseLang.class);
            when(server.getLanguage()).thenReturn(language);
            when(language.translate(anyString(), any(Object[].class))).thenReturn("notification");
            servers.when(Server::getInstance).thenReturn(server);
            player.configure(server);
            doReturn(true).when(player).isAlive();
            doReturn(true).when(player).isAcceptingInputPackets();
            doReturn(inputMode).when(player).isMainThreadInputEnabled();
            doReturn("actor").when(player).getDisplayName();
            doNothing().when(player).resetCraftingGridType();
        }

        private void onChat(Runnable action) {
            doAnswer(call -> {
                action.run();
                return null;
            }).when(plugins).callEvent(any(PlayerChatEvent.class));
        }

        @Override
        public void close() {
            servers.close();
        }
    }

    static class ProbePlayer extends Player {
        ProbePlayer() {
            super(null, 1L, new InetSocketAddress("127.0.0.1", 19132));
        }

        void configure(Server server) {
            this.server = server;
            this.spawned = true;
            this.messageCounter = 2;
        }

        int counter() {
            return messageCounter;
        }
    }
}
