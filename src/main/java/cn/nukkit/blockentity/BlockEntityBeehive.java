package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

public class BlockEntityBeehive extends BlockEntity {

    protected boolean shouldSpawnBees;
    protected List<Occupant> occupants;

    public BlockEntityBeehive(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("ShouldSpawnBees")) {
            shouldSpawnBees = namedTag.getBoolean("ShouldSpawnBees");
        } else {
            shouldSpawnBees = false;
        }

        occupants = new LinkedList<>();
        if (namedTag.contains("Occupants")) {
            ListTag<CompoundTag> occupants = namedTag.getList("Occupants", CompoundTag.class);
            for (CompoundTag tag : occupants.getAll()) {
                CompoundTag saveData = tag.getCompound("SaveData");
                if (!saveData.contains("id")) {
                    continue;
                }
                this.occupants.add(new Occupant(tag.getInt("TicksLeftToStay"), saveData));
            }
        }

        super.initBlockEntity();

        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("ShouldSpawnBees", shouldSpawnBees);

        if (occupants.isEmpty()) {
            namedTag.remove("Occupants");
        } else {
            ListTag<CompoundTag> occupants = new ListTag<>("Occupants");
            for (Occupant data : this.occupants) {
                occupants.add(new CompoundTag()
                        .putInt("TicksLeftToStay", data.ticksLeftToStay)
                        .putString("ActorIdentifier", "minecraft:bee<>") // hardcode
                        .putCompound("SaveData", data.saveData.clone()));
            }
            namedTag.putList(occupants);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BEEHIVE || blockId == Block.BEE_NEST;
    }

    @Override
    public boolean onUpdate() {
        if (occupants.isEmpty()) {
            return false;
        }

        for (int i = 0; i < occupants.size(); i++) {
            Occupant beeData = occupants.get(i);

            if (beeData.ticksLeftToStay > 0) {
                --beeData.ticksLeftToStay;
                continue;
            }

            revive(i, false);
        }

        return !occupants.isEmpty();
    }

    public boolean isShouldSpawnBees() {
        return shouldSpawnBees;
    }

    public void setShouldSpawnBees(boolean shouldSpawnBees) {
        this.shouldSpawnBees = shouldSpawnBees;
    }

    /**
     * Store a bee.
     * @param bee bee entity
     * @return success
     */
    public boolean tryAdmit(Entity bee) {
        if (bee.getNetworkId() != EntityID.BEE) {
            return false;
        }

        if (occupants.size() >= 3) {
            return false;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_BEEHIVE_ENTER);

        bee.saveNBT();
        occupants.add(new Occupant(bee.getDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_POWERED) ? 2400 : 600, bee.namedTag.clone()));
        bee.close();

        scheduleUpdate();
        setDirty();
        return true;
    }

    /**
     * Release the specified bee.
     * @param index occupant index
     * @return success
     */
    public boolean revive(int index) {
        return revive(index, false);
    }

    /**
     * Release the specified bee.
     * @param index occupant index
     * @param emergency force exit
     * @return success
     */
    public boolean revive(int index, boolean emergency) {
        if (occupants.size() <= index) {
            return false;
        }

        if (!emergency && (level.isRaining() || level.isNight())) {
            return false;
        }

        Occupant beeData = occupants.get(index);
        CompoundTag saveData = beeData.saveData;
        saveData.putList(new ListTag<DoubleTag>("Pos") //TODO: find air
                .add(new DoubleTag("", x))
                .add(new DoubleTag("", y))
                .add(new DoubleTag("", z)));
        saveData.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0)));

        Entity entity = Entity.createEntity(saveData.getString("id"), getChunk(), saveData);
        if (entity != null) {
            entity.spawnToAll();
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_BEEHIVE_EXIT);

        occupants.remove(index);
        setDirty();
        return true;
    }

    /**
     * Release all bees.
     * Execute event "minecraft:exited_disturbed_hive" or "minecraft:exited_hive_on_fire".
     * @return success
     */
    public boolean evictAll() {
        if (occupants.isEmpty()) {
            return false;
        }

        for (int i = 0; i < occupants.size(); i++) {
            revive(i, true);
        }
        return true;
    }

    /**
     * @param index occupant index
     */
    public Occupant getOccupant(int index) {
        if (occupants.size() <= index) {
            return null;
        }
        return occupants.get(index);
    }

    public int getTotalOccupants() {
        return occupants.size();
    }

    @AllArgsConstructor
    @Data
    public static class Occupant {
        int ticksLeftToStay;
        CompoundTag saveData;
    }
}
