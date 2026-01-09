package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityChalkboard extends BlockEntitySpawnable {
    public static final String TAG_ON_GROUND = "OnGround";
    public static final String TAG_SIZE = "Size";
    public static final String TAG_BASE_X = "BaseX";
    public static final String TAG_BASE_Y = "BaseY";
    public static final String TAG_BASE_Z = "BaseZ";
    public static final String TAG_LOCKED = "Locked";
    public static final String TAG_TEXT = "Text";
    public static final String TAG_OWNER = "Owner";

    public static final int SIZE_1X1 = 0;
    public static final int SIZE_2X1 = 1;
    public static final int SIZE_3X2 = 2;

    public static final int MAX_TEXT_LENGTH = 2000;

    private int baseX;
    private int baseY;
    private int baseZ;

    private boolean onGround;
    private int size;

    private boolean locked;

    private String text;
    private long ownerEntityId;

    public BlockEntityChalkboard(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.CHALKBOARD_BLOCK;
    }

    @Override
    protected void initBlockEntity() {
        baseX = namedTag.getInt(TAG_BASE_X);
        baseY = namedTag.getInt(TAG_BASE_Y);
        baseZ = namedTag.getInt(TAG_BASE_Z);

        if (!isBase()) {
            super.initBlockEntity();
            scheduleUpdate();
            return;
        }

        onGround = namedTag.getBoolean(TAG_ON_GROUND);
        size = namedTag.getInt(TAG_SIZE, SIZE_1X1);

        locked = namedTag.getBoolean(TAG_LOCKED);

        text = namedTag.getString(TAG_TEXT);
        ownerEntityId = namedTag.getLong(TAG_OWNER);

        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt(TAG_BASE_X, baseX);
        namedTag.putInt(TAG_BASE_Y, baseY);
        namedTag.putInt(TAG_BASE_Z, baseZ);

        if (!isBase()) {
            return;
        }

        namedTag.putBoolean(TAG_ON_GROUND, onGround);
        namedTag.putInt(TAG_SIZE, size);

        namedTag.putBoolean(TAG_LOCKED, locked);

        namedTag.putString(TAG_TEXT, text);
        namedTag.putLong(TAG_OWNER, ownerEntityId);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CHALKBOARD;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        return getDefaultCompound(this, CHALKBOARD_BLOCK)
                .putInt(TAG_BASE_X, baseX)
                .putInt(TAG_BASE_Y, baseY)
                .putInt(TAG_BASE_Z, baseZ)
                .putBoolean(TAG_ON_GROUND, onGround)
                .putInt(TAG_SIZE, size)
                .putBoolean(TAG_LOCKED, locked)
                .putString(TAG_TEXT, text)
                .putLong(TAG_OWNER, ownerEntityId);
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!CHALKBOARD_BLOCK.equals(nbt.getString("id"))) {
            return false;
        }

        if (locked && player.isSurvivalLike()) {
            return false;
        }

        String text = nbt.getString(TAG_TEXT);
        if (text.length() > MAX_TEXT_LENGTH) {
            text = text.substring(0, MAX_TEXT_LENGTH);
        }
        this.text = text;

        spawnToAll();
        return true;
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        //TODO: ChalkboardBlockActor::validate
        return true;
    }

    public boolean isBase() {
        return getFloorX() == baseX && getFloorY() == baseY && getFloorZ() == baseZ;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public int getBaseZ() {
        return baseZ;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public int getSize() {
        return size;
    }
}
