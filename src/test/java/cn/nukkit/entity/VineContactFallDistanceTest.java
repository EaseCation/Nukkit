package cn.nukkit.entity;

import cn.nukkit.block.Block;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class VineContactFallDistanceTest {
    @BeforeAll
    static void initializeBlocks() { Block.init(); }

    @Test
    void touchingNorthVineResetsTheAccumulatedFallHeight() {
        Entity entity = entityAt(new SimpleAxisAlignedBB(0.2, 64, -0.2, 0.8, 65.8, 0.04));

        entity.checkBlockCollision();

        assertEquals(1, entity.getCollisionBlocks().size());
        assertEquals(Block.VINE, entity.getCollisionBlocks().getFirst().getId());
        assertEquals(HeightRange.MINIMUM.getMinY(), entity.highestPosition);
        assertTrue(entity.onGround);
    }

    @Test
    void fallingBesideTheNorthVineDoesNotEraseTheFallHeight() {
        Entity entity = entityAt(new SimpleAxisAlignedBB(0.2, 64, 0.2, 0.8, 65.8, 0.8));

        entity.checkBlockCollision();

        assertTrue(entity.getCollisionBlocks().isEmpty());
        assertEquals(107, entity.highestPosition);
        assertFalse(entity.onGround);
    }

    @Test
    void everyNonemptyVineOrientationContainsItsAttachedFacesInsideTheBlock() {
        // Legacy metadata: south=1, west=2, north=4, east=8. Faces are 1/16 thick.
        int[] faces = {1, 2, 4, 8};
        Vector3[] faceCenters = {
                new Vector3(0.5, 0.5, 0.96875), new Vector3(0.03125, 0.5, 0.5),
                new Vector3(0.5, 0.5, 0.03125), new Vector3(0.96875, 0.5, 0.5)
        };
        for (int state = 1; state <= 15; state++) {
            AxisAlignedBB box = Block.get(Block.VINE, state).getCollisionBoundingBox();
            assertNotNull(box, "state " + state);
            assertTrue(box.getMinX() >= 0 && box.getMaxX() <= 1 && box.getMinX() < box.getMaxX(), "x state " + state);
            assertTrue(box.getMinY() >= 0 && box.getMaxY() <= 1 && box.getMinY() < box.getMaxY(), "y state " + state);
            assertTrue(box.getMinZ() >= 0 && box.getMaxZ() <= 1 && box.getMinZ() < box.getMaxZ(), "z state " + state);
            for (int face = 0; face < faces.length; face++) {
                if ((state & faces[face]) != 0) assertTrue(box.isVectorInside(faceCenters[face]), "face " + faces[face] + " state " + state);
            }
        }
        AxisAlignedBB north = Block.get(Block.VINE, 4).getCollisionBoundingBox();
        assertEquals(0, north.getMinZ());
        assertEquals(0.0625, north.getMaxZ());
    }

    private Entity entityAt(AxisAlignedBB box) {
        Entity entity = mock(Entity.class, CALLS_REAL_METHODS);
        Level level = mock(Level.class);
        Block vine = Block.get(Block.VINE, 4);
        vine.y = 64;
        vine.level = level;
        Block air = Block.get(Block.AIR);
        when(level.getHeightRange()).thenReturn(HeightRange.MINIMUM);
        when(level.getBlock(anyInt(), anyInt(), anyInt())).thenAnswer(call ->
                call.<Integer>getArgument(0) == 0 && call.<Integer>getArgument(1) == 64 && call.<Integer>getArgument(2) == 0 ? vine : air);
        when(level.getExtraBlock(anyInt(), anyInt(), anyInt())).thenReturn(air);
        entity.level = level;
        entity.boundingBox = box;
        entity.highestPosition = 107;
        return entity;
    }
}
