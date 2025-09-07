package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCampfire;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityCampfire extends BlockEntitySpawnable {

    public static final int SLOT_COUNT = 4;

    protected Item[] items;
    protected int[] itemTime;

    public BlockEntityCampfire(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.CAMPFIRE;
    }

    @Override
    protected void initBlockEntity() {
        items = new Item[SLOT_COUNT];
        itemTime = new int[SLOT_COUNT];

        boolean ticking = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            int tagNum = i + 1;
            String itemTag = "Item" + tagNum;

            if (namedTag.contains(itemTag)) {
                items[i] = NBTIO.getItemHelper(namedTag.getCompound(itemTag));
                itemTime[i] = namedTag.getInt("ItemTime" + tagNum);
                ticking = true;
            }
        }

        super.initBlockEntity();

        if (ticking && canCooking()) {
            scheduleUpdate();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        for (int i = 0; i < SLOT_COUNT; i++) {
            int tagNum = i + 1;

            Item item = getItem(i);
            if (item == null) {
                namedTag.remove("Item" + tagNum);
                namedTag.putInt("ItemTime" + tagNum, 0);
                continue;
            }

            namedTag.putCompound("Item" + tagNum, NBTIO.putItemHelper(item));
            namedTag.putInt("ItemTime" + tagNum, itemTime[i]);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_CAMPFIRE || blockId == Block.BLOCK_SOUL_CAMPFIRE;
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

        if (!canCooking()) {
            return false;
        }

        boolean itemChanged = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);
            if (item == null) {
                continue;
            }

            int cookingTime = getCookingTime(i);
            if (++cookingTime < 600) {
                setCookingTime(i, cookingTime);
                continue;
            }

            if (!level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                continue;
            }

            FurnaceRecipe recipe = server.getCraftingManager().matchFurnaceRecipe(item, RecipeTag.CAMPFIRE);
            if (recipe != null) {
                FurnaceSmeltEvent event = new FurnaceSmeltEvent(this, item, recipe.getResult());
                this.server.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    continue;
                }
                item = event.getResult();
            }

            if (item != null && item.getId() != ItemID.AIR) {
                level.dropItem(this, item);
            }

            items[i] = null;
            setCookingTime(i, 0);

            itemChanged = true;
        }

        if (itemChanged) {
            setDirty();
            spawnToAll();
        }

        if (ThreadLocalRandom.current().nextInt(50) == 0) {
            level.addLevelSoundEvent(add(0.5, 0.2, 0.5), LevelSoundEventPacket.SOUND_BLOCK_CAMPFIRE_CRACKLE);
        }

        return true;
    }

    @Override
    public void onBreak() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);
            if (item == null) {
                continue;
            }
            items[i] = null;

            level.dropItem(this, item);
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, CAMPFIRE);

        for (int i = 0; i < SLOT_COUNT; i++) {
            int tagNum = i + 1;
            nbt.putInt("ItemTime" + tagNum, itemTime[i]);

            Item item = getItem(i);
            if (item == null) {
                continue;
            }
            nbt.putCompound("Item" + tagNum, NBTIO.putItemHelper(item));
        }

        return nbt;
    }

    protected boolean canCooking() {
        Block block = getBlock();
        return block instanceof BlockCampfire && !((BlockCampfire) block).isExtinguished();
    }

    public int getCookingTime(int index) {
        return itemTime[index];
    }

    public void setCookingTime(int index, int cookingTime) {
        itemTime[index] = cookingTime;
    }

    @Nullable
    public Item getItem(int index) {
        return items[index];
    }

    public void setItem(int index, @Nullable Item item) {
        items[index] = item;
        setDirty();
    }

    /**
     * @return success
     */
    public boolean tryAddItem(Item item, RecipeTag recipeTag) {
        if (server.getCraftingManager().matchFurnaceRecipe(item, recipeTag) == null) {
            return false;
        }

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (getItem(i) != null) {
                continue;
            }

            Item item1 = item.clone();
            item1.setCount(1);
            setItem(i, item1);
            setCookingTime(i, 0);

            scheduleUpdate();
            return true;
        }

        return false;
    }

    public void dropAllItems() {
        boolean dirty = false;

        for (int i = 0; i < SLOT_COUNT; i++) {
            Item item = getItem(i);

            if (item == null) {
                continue;
            }

            level.dropItem(this, item);

            items[i] = null;
            setCookingTime(i, 0);
            dirty = true;
        }

        if (dirty) {
            setDirty();
            spawnToAll();
        }
    }
}
