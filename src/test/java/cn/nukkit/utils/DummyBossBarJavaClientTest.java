package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.network.protocol.DataPacket;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DummyBossBarJavaClientTest {

    @Test
    void javaClientReceivesOnlyBossEventsForCompleteLifecycle() {
        final Player player = javaPlayer();
        final DummyBossBar bossBar = DummyBossBar.builder(player).text("first").length(50).build();

        bossBar.create();
        bossBar.setText("second");
        bossBar.setLength(75);
        bossBar.reshow();
        bossBar.destroy();

        final ArgumentCaptor<DataPacket> packets = ArgumentCaptor.forClass(DataPacket.class);
        verify(player, times(5)).dataPacket(packets.capture());
        final List<DataPacket> values = packets.getAllValues();
        assertTrue(values.stream().allMatch(BossEventPacket.class::isInstance));
        assertEquals(BossEventPacket.TYPE_SHOW, ((BossEventPacket) values.get(0)).type);
        assertEquals(BossEventPacket.TYPE_TITLE, ((BossEventPacket) values.get(1)).type);
        assertEquals(BossEventPacket.TYPE_HEALTH_PERCENT, ((BossEventPacket) values.get(2)).type);
        assertEquals(BossEventPacket.TYPE_SHOW, ((BossEventPacket) values.get(3)).type);
        assertEquals(BossEventPacket.TYPE_HIDE, ((BossEventPacket) values.get(4)).type);
    }

    @Test
    void multipleJavaBossBarsKeepIndependentIds() {
        final Player player = javaPlayer();
        DummyBossBar.builder(player).text("first").build().create();
        DummyBossBar.builder(player).text("second").build().create();

        final ArgumentCaptor<DataPacket> packets = ArgumentCaptor.forClass(DataPacket.class);
        verify(player, times(2)).dataPacket(packets.capture());
        final BossEventPacket first = (BossEventPacket) packets.getAllValues().get(0);
        final BossEventPacket second = (BossEventPacket) packets.getAllValues().get(1);
        assertNotEquals(first.bossEid, second.bossEid);
    }

    private static Player javaPlayer() {
        final Player player = mock(Player.class);
        when(player.isJavaClient()).thenReturn(true);
        when(player.dataPacket(any())).thenReturn(true);
        return player;
    }
}
