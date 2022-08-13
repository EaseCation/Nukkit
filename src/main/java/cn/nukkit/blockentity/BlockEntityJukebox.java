package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    @Nullable
    private ItemRecord recordItem;

    private boolean finishedRecording;
    private long ticksPlaying;

    public BlockEntityJukebox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        boolean hasRecord = false;
        if (namedTag.contains("RecordItem")) {
            Item item = NBTIO.getItemHelper(namedTag.getCompound("RecordItem"));
            if (item instanceof ItemRecord && !item.isNull()) {
                hasRecord = true;
                this.recordItem = (ItemRecord) item;
            }
        }

        if (hasRecord) {
            if (namedTag.contains("FinishedRecording")) {
                this.finishedRecording = namedTag.getBoolean("FinishedRecording");
            } else {
                this.finishedRecording = true;
            }

            if (namedTag.contains("TicksPlaying")) {
                this.ticksPlaying = namedTag.getLong("TicksPlaying");
            } else {
                this.ticksPlaying = 0;
            }

//            this.scheduleUpdate();
        } else {
            this.recordItem = null;
            this.finishedRecording = true;
            this.ticksPlaying = 0;
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.JUKEBOX;
    }

    public void setRecordItem(@Nullable ItemRecord recordItem) {
        if (recordItem == null || recordItem.isNull()) {
            this.recordItem = null;
            this.finishedRecording = true;
            this.ticksPlaying = 0;
            return;
        }

        this.recordItem = recordItem;
        this.finishedRecording = false;
        this.ticksPlaying = 1;
    }

    @Nullable
    public Item getRecordItem() {
        return recordItem;
    }

    public void play() {
        if (this.recordItem == null) {
            this.stop();
            return;
        }

        this.finishedRecording = false;
        this.ticksPlaying = 1;
        this.getLevel().addLevelSoundEvent(this.blockCenter(), recordItem.getSoundEvent());

        for (Player player : this.getLevel().getChunkPlayers(this.getChunkX(), this.getChunkZ()).values()) {
            player.sendJukeboxPopup(new TranslationContainer("record.nowPlaying", "%item." + recordItem.getTranslationIdentifier() + ".desc"));
        }

//        this.scheduleUpdate();
    }

    public void stop() {
        this.finishedRecording = true;
        this.ticksPlaying = 0;
        this.getLevel().addLevelSoundEvent(this.blockCenter(), LevelSoundEventPacket.SOUND_STOP_RECORD);
    }

    public void dropItem() {
        if (this.recordItem == null) {
            return;
        }

        this.level.dropItem(this.up(), this.recordItem);
        this.recordItem = null;
        this.stop();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.recordItem == null) {
            this.namedTag.remove("RecordItem");
        } else {
            this.namedTag.putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
//            this.namedTag.putBoolean("FinishedRecording", this.finishedRecording);
//            this.namedTag.putLong("TicksPlaying", this.ticksPlaying);
        }

        this.namedTag.remove("FinishedRecording"); // runtime only
        this.namedTag.remove("TicksPlaying"); // runtime only
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, JUKEBOX)
                .putBoolean("FinishedRecording", this.finishedRecording)
                .putLong("TicksPlaying", this.ticksPlaying);

        if (this.recordItem != null) {
            nbt.putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
        }

        return nbt;
    }

    @Override
    public boolean onUpdate() {
        if (this.finishedRecording) {
            this.ticksPlaying = 0;
            return false;
        }

        ++this.ticksPlaying;
        return true;
    }
}
