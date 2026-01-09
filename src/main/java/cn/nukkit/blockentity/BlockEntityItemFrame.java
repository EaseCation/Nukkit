package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockEntityItemFrame extends BlockEntitySpawnable {
    @Nullable
    private Item item;

    public BlockEntityItemFrame(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.ITEM_FRAME;
    }

    @Override
    protected void initBlockEntity() {
        Item item = null;
        CompoundTag itemTag = this.namedTag.getCompound("Item", null);
        if (itemTag != null) {
            item = NBTIO.getItemHelper(itemTag);

            if (item.isNull()) {
                item = null;
            }
        }
        this.item = item;

        if (!namedTag.contains("ItemRotation")) {
            namedTag.putFloat("ItemRotation", 0);
        } else {
            Tag tag = namedTag.get("ItemRotation");
            if (tag instanceof ByteTag) {
                namedTag.putFloat("ItemRotation", (((ByteTag) tag).data * 360 / 8) % 360);
            }
        }
        if (!namedTag.contains("ItemDropChance")) {
            namedTag.putFloat("ItemDropChance", 1.0f);
        }

        this.level.updateComparatorOutputLevel(this);

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (!hasItem()) {
            namedTag.remove("Item");
            return;
        }

        namedTag.putCompound("Item", NBTIO.putItemHelper(item));
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.FRAME;
    }

    public float getItemRotation() {
        return this.namedTag.getFloat("ItemRotation");
    }

    private void setItemRotationInternal(float itemRotation) {
        this.namedTag.putFloat("ItemRotation", itemRotation % 360);
    }

    public void setItemRotation(float itemRotation) {
        this.setItemRotationInternal(itemRotation);

        this.level.updateComparatorOutputLevel(this);

        this.setChanged(this.hasItem());
    }

    public void rotateItem() {
        this.setItemRotation(this.getItemRotation() + 45);
    }

    public Item getItem() {
        if (!this.hasItem()) {
            return Items.air();
        }

        return item.clone();
    }

    public boolean hasItem() {
        return item != null && !item.isNull();
    }

    public void setItem(@Nullable Item item) {
        this.setItem(item, true);
    }

    public void setItem(@Nullable Item item, boolean setChanged) {
        this.item = item;

        if (setChanged) {
            this.setChanged(true);
        }

        this.level.updateComparatorOutputLevel(this);
    }

    public float getItemDropChance() {
        return this.namedTag.getFloat("ItemDropChance");
    }

    public void setItemDropChance(float chance) {
        this.namedTag.putFloat("ItemDropChance", chance);
    }

    private void setChanged(boolean spawn) {
        if (spawn) {
            this.spawnToAll();
        }
        if (this.chunk != null) {
            this.chunk.setChanged();
        }
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = getDefaultCompound(this, getBlockEntityId());

        if (this.hasItem()) {
            nbt.putCompound("Item", NBTIO.putItemHelper(item));
            nbt.putFloat("ItemRotation", this.getItemRotation());
            nbt.putFloat("ItemDropChance", this.getItemDropChance());
        }

        return nbt;
    }

    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().getId() == ItemID.AIR ? 0 : (int) this.getItemRotation() / 45 + 1;
    }

    public boolean dropItem(Player player) {
        if (hasItem()) {
            Item item = this.getItem();

            if (player != null) {
                ItemFrameDropItemEvent event = new ItemFrameDropItemEvent(player, this.getBlock(), this, item);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.spawnTo(player);
                    return true;
                }
            }

            if ((player == null || player.isSurvival()) && this.getItemDropChance() > ThreadLocalRandom.current().nextFloat()) {
                this.level.dropItem(this.add(0.5, 0, 0.5), item);
            }

            this.setItemRotationInternal(0);
            this.setItem(null);

            this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED);
            return true;
        }
        return false;
    }

    protected String getBlockEntityId() {
        return ITEM_FRAME;
    }
}
