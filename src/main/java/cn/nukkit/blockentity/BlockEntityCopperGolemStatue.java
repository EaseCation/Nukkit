package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.AllArgsConstructor;
import lombok.Data;

public class BlockEntityCopperGolemStatue extends BlockEntitySpawnable {
    public static final int POSE_STANDING = 0;
    public static final int POSE_SITTING = 1;
    public static final int POSE_RUNNING = 2;
    public static final int POSE_STAR = 3;

    private EntityData entityData;
    private int pose;

    public BlockEntityCopperGolemStatue(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.COPPER_GOLEM_STATUE;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("Actor")) {
            CompoundTag entityData = namedTag.getCompound("Actor");
            String identifier = entityData.getString("ActorIdentifier", "minecraft:copper_golem<>");
            CompoundTag saveData = entityData.getCompound("SaveData");
            this.entityData = new EntityData(identifier, saveData);
        } else {
            this.entityData = new EntityData("minecraft:copper_golem<>", new CompoundTag());
        }

        pose = namedTag.getInt("Pose", POSE_STANDING) & 3;

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (entityData != null) {
            CompoundTag entityData = new CompoundTag();
            entityData.putString("ActorIdentifier", this.entityData.getIdentifier());
            entityData.putCompound("SaveData", this.entityData.getSaveData());
            namedTag.putCompound("Actor", entityData);
        }

        namedTag.putInt("Pose", pose);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.COPPER_GOLEM_STATUE || blockId == Block.WAXED_COPPER_GOLEM_STATUE
                || blockId == Block.EXPOSED_COPPER_GOLEM_STATUE || blockId == Block.WAXED_EXPOSED_COPPER_GOLEM_STATUE
                || blockId == Block.WEATHERED_COPPER_GOLEM_STATUE || blockId == Block.WAXED_WEATHERED_COPPER_GOLEM_STATUE
                || blockId == Block.OXIDIZED_COPPER_GOLEM_STATUE || blockId == Block.WAXED_OXIDIZED_COPPER_GOLEM_STATUE;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        return getDefaultCompound(this, COPPER_GOLEM_STATUE)
                .putInt("Pose", pose);
    }

    public int getPose() {
        return pose;
    }

    public void setPose(int pose) {
        this.pose = pose & 3;
    }

    public void changePose() {
        setPose(getPose() + 1);
    }

    @AllArgsConstructor
    @Data
    public static class EntityData {
        String identifier;
        CompoundTag saveData;
    }
}
