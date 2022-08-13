package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;

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
    protected BlockVector3 exitPortal;

    public BlockEntityEndGateway(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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

        ListTag<IntTag> blockPos = exitPortal.toNbt();
        blockPos.setName(TAG_EXIT_PORTAL);
        namedTag.putList(blockPos);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.END_GATEWAY;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        ListTag<IntTag> blockPos = exitPortal.toNbt();
        blockPos.setName(TAG_EXIT_PORTAL);

        return getDefaultCompound(this, END_GATEWAY)
                .putInt(TAG_AGE, age)
                .putList(blockPos);
    }

    @Override
    public boolean onUpdate() {
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
}
