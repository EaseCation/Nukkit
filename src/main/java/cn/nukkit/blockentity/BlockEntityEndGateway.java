package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;

import javax.annotation.Nullable;

public class BlockEntityEndGateway extends BlockEntitySpawnable {
    /**
     * int.
     */
    public static final String TAG_AGE = "Age";
    /**
     * Block position.
     * list.
     */
    public static final String TAG_EXIT_PORTAL = "ExitPortal";

    protected int age;
    @Nullable
    protected BlockVector3 exitPortal;

    public BlockEntityEndGateway(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.END_GATEWAY;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains(TAG_AGE)) {
            age = namedTag.getInt(TAG_AGE);
        } else {
            age = 0;
        }
        if (namedTag.contains(TAG_EXIT_PORTAL)) {
            exitPortal = BlockVector3.fromNbt(namedTag.getList(TAG_EXIT_PORTAL, IntTag.class));
        } else {
            exitPortal = new BlockVector3();
        }

        super.initBlockEntity();

//        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt(TAG_AGE, age);

        if (exitPortal != null) {
            namedTag.putList(TAG_EXIT_PORTAL, exitPortal.toNbt());
        } else {
            namedTag.remove(TAG_EXIT_PORTAL);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.END_GATEWAY;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag tag = getDefaultCompound(this, END_GATEWAY)
                .putInt(TAG_AGE, age);

        if (exitPortal != null) {
            tag.putList(TAG_EXIT_PORTAL, exitPortal.toNbt());
        }

        return tag;
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

        int age = this.age;
        if (age++ < 200) {
            this.age = age;

            if (age == 200) {
                setDirty();
                return false;
            }

            return true;
        }

        //TODO: EndGatewayBlockActor::teleportEntity
        return false;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Nullable
    public BlockVector3 getExitPortal() {
        return exitPortal;
    }

    public void setExitPortal(@Nullable BlockVector3 pos) {
        this.exitPortal = pos;
    }
}
