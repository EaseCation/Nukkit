package cn.nukkit;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.attribute.AttributeModifiers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SprintStatePublicationTest {
    @BeforeAll static void initializeAttributes() { Attribute.init(); }

    @Test void startPublicationSeesTheCommittedBoost() {
        Fixture fixture = new Fixture(true);
        fixture.publication = () -> assertEquals(0.13f, fixture.player.getMovementSpeedAttribute().getModifiedValue(), 1e-6f);
        assertTrue(fixture.player.setSprinting(true));
    }

    @Test void stopPublicationSeesTheRemovedBoost() {
        Fixture fixture = new Fixture(true);
        fixture.player.setSprinting(true);
        fixture.publication = () -> assertEquals(0.10f, fixture.player.getMovementSpeedAttribute().getModifiedValue(), 1e-6f);
        assertTrue(fixture.player.setSprinting(false));
    }

    @Test void nestedResetDoesNotLeaveAnOrphanBoost() {
        Fixture fixture = new Fixture(true);
        fixture.publication = () -> fixture.player.setSprinting(false);
        assertTrue(fixture.player.setSprinting(true));
        assertFalse(fixture.player.isSprinting());
        assertFalse(fixture.player.getMovementSpeedAttribute().getModifiers().containsKey(AttributeModifiers.SPRINTING_BOOST.getId()));
        assertEquals(0.10f, fixture.player.getMovementSpeedAttribute().getModifiedValue(), 1e-6f);
    }

    @Test void nestedStartKeepsItsOwnBoost() {
        Fixture fixture = new Fixture(true);
        fixture.player.setSprinting(true);
        fixture.publication = () -> fixture.player.setSprinting(true);
        assertTrue(fixture.player.setSprinting(false));
        assertTrue(fixture.player.isSprinting());
        assertEquals(0.13f, fixture.player.getMovementSpeedAttribute().getModifiedValue(), 1e-6f);
    }

    @Test void repeatedStartDoesNotStackBoostOrPublishAgain() {
        Fixture fixture = new Fixture(true);
        assertTrue(fixture.player.setSprinting(true));
        fixture.publication = () -> fail("Unchanged flag must not publish");
        assertFalse(fixture.player.setSprinting(true));
        assertEquals(1, fixture.player.getMovementSpeedAttribute().getModifiers().size());
        assertEquals(0.13f, fixture.player.getMovementSpeedAttribute().getModifiedValue(), 1e-6f);
    }

    @Test void disabledModeRetainsTheOriginalNestedResetOutcome() {
        Fixture fixture = new Fixture(false);
        fixture.publication = () -> fixture.player.setSprinting(false);
        assertTrue(fixture.player.setSprinting(true));
        assertFalse(fixture.player.isSprinting());
        assertEquals(0.13f, fixture.player.getMovementSpeedAttribute().getModifiedValue(), 1e-6f);
    }

    private static class Fixture {
        final ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        final AtomicBoolean flag = new AtomicBoolean();
        Runnable publication = () -> { };

        Fixture(boolean enabled) {
            player.configure();
            doReturn(enabled).when(player).isMainThreadInputEnabled();
            doAnswer(call -> flag.get()).when(player).isSprinting();
            // 只替代Entity的元数据存储/发布边界，疾跑setter与属性实现均执行真实代码。
            doAnswer(call -> {
                assertEquals(Entity.DATA_FLAG_SPRINTING, (int)call.getArgument(0));
                boolean value = call.getArgument(1);
                if (flag.getAndSet(value) == value) return false;
                Runnable callback = publication;
                publication = () -> { };
                callback.run();
                return true;
            }).when(player).setDataFlag(anyInt(), anyBoolean());
        }
    }

    static class ProbePlayer extends Player {
        ProbePlayer() { super(null, 0L, new InetSocketAddress("127.0.0.1", 19132)); }
        void configure() { this.movementSpeedAttribute = Attribute.getAttribute(Attribute.MOVEMENT); }
    }
}
