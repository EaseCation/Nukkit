package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockEntityItemFrame extends BlockEntitySpawnable {

    public BlockEntityItemFrame(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (this.namedTag.contains("Item")) {
            CompoundTag item = this.namedTag.getCompound("Item");
            if (item.getShort("id") == ItemID.AIR) {
                this.namedTag.remove("Item");
            }
        }

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
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_FRAME;
    }

    public float getItemRotation() {
        return this.namedTag.getFloat("ItemRotation");
    }

    public void setItemRotation(float itemRotation) {
        this.namedTag.putFloat("ItemRotation", itemRotation % 360);
        this.level.updateComparatorOutputLevel(this);
        this.setChanged(this.hasItem());
    }

    public void rotateItem() {
        this.setItemRotation(this.getItemRotation() + 45);
    }

    public Item getItem() {
        if (!this.hasItem()) {
            return Item.get(ItemID.AIR);
        }

        CompoundTag NBTTag = this.namedTag.getCompound("Item");
        return NBTIO.getItemHelper(NBTTag);
    }

    public boolean hasItem() {
        return this.namedTag.contains("Item");
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        if (item.isNull()) {
            this.namedTag.remove("Item");
        } else {
            this.namedTag.putCompound("Item", NBTIO.putItemHelper(item));
        }

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
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, ITEM_FRAME);

        if (this.hasItem()) {
            nbt.putCompound("Item", this.namedTag.getCompound("Item"));
            nbt.putFloat("ItemRotation", this.getItemRotation());
            nbt.putFloat("ItemDropChance", this.getItemDropChance());
        }

        return nbt;
    }

    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().getId() == ItemID.AIR ? 0 : (int) this.getItemRotation() / 45 + 1;
    }

    public boolean dropItem(Player player) {
        Item item = this.getItem();
        if (item != null && item.getId() != Item.AIR) {
            if (player != null) {
                ItemFrameDropItemEvent event = new ItemFrameDropItemEvent(player, this.getBlock(), this, item);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.spawnTo(player);
                    return true;
                }
            }

            if (this.getItemDropChance() > ThreadLocalRandom.current().nextFloat()) {
                this.level.dropItem(this.add(0.5, 0, 0.5), item);
            }
            this.setItem(Item.get(Item.AIR));
            this.setItemRotation(0);
            this.level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED);
            return true;
        }
        return false;
    }
}
