package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class BlockEntityBarrel extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected BarrelInventory inventory;

    public BlockEntityBarrel(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new BarrelInventory(this);

        ListTag<CompoundTag> items = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag tag = items.get(i);
            Item item = NBTIO.getItemHelper(tag);
            this.inventory.slots.put(tag.getByte("Slot"), item);
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BARREL;
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        inventory.clearAll();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, BARREL);

        if (this.hasName()) {
            nbt.put("CustomName", this.namedTag.get("CustomName"));
        }

        return nbt;
    }

    @Override
    public BarrelInventory getInventory() {
        return this.inventory;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return Item.get(ItemID.AIR);
        }
        CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
        return NBTIO.getItemHelper(data);
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);
        CompoundTag tag = NBTIO.putItemHelper(item, index);

        if (item.isNull()) {
            if (i >= 0) {
                this.namedTag.getList("Items").remove(i);
            }
        } else if (i < 0) {
            this.namedTag.getList("Items", CompoundTag.class).add(tag);
        } else {
            this.namedTag.getList("Items", CompoundTag.class).add(i, tag);
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
