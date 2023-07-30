package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MovingObjectPosition {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_BLOCK = 0;
    public static final int TYPE_ENTITY = 1;

    /**
     * 0 = block, 1 = entity
     */
    public int typeOfHit;

    public int blockX;
    public int blockY;
    public int blockZ;

    /**
     * Which side was hit. If its -1 then it went the full length of the ray trace.
     * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
     */
    public int sideHit = -1;

    public Block block;

    public Vector3 hitVector;

    public Entity entityHit;

    public boolean isNone() {
        return typeOfHit == TYPE_NONE;
    }

    public boolean isBlock() {
        return typeOfHit == TYPE_BLOCK;
    }

    public boolean isEntity() {
        return typeOfHit == TYPE_ENTITY;
    }

    public static MovingObjectPosition none() {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = TYPE_NONE;
        return objectPosition;
    }

    public static MovingObjectPosition fromBlock(int x, int y, int z, int side, Vector3 hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = TYPE_BLOCK;
        objectPosition.blockX = x;
        objectPosition.blockY = y;
        objectPosition.blockZ = z;
        objectPosition.sideHit = side;
        objectPosition.hitVector = new Vector3(hitVector.x, hitVector.y, hitVector.z);
        return objectPosition;
    }

    public static MovingObjectPosition fromBlock(Block block, int side, Vector3 hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = TYPE_ENTITY;
        objectPosition.block = block;
        objectPosition.blockX = block.getFloorX();
        objectPosition.blockY = block.getFloorY();
        objectPosition.blockZ = block.getFloorZ();
        objectPosition.sideHit = side;
        objectPosition.hitVector = hitVector.copyVec();
        return objectPosition;
    }

    public static MovingObjectPosition fromEntity(Entity entity) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = TYPE_ENTITY;
        objectPosition.entityHit = entity;
        objectPosition.hitVector = new Vector3(entity.x, entity.y, entity.z);
        return objectPosition;
    }
}
