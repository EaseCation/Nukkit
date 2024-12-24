package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFullNames;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;

import javax.annotation.Nullable;

public class BlockEntityDecoratedPot extends BlockEntitySpawnable {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_INSERT_FAIL = 1;
    public static final int ANIMATION_INSERT_SUCCESS = 2;

    @Nullable
    private String[] sherds;

    @Nullable
    private Item item;

    private int animation;

    public BlockEntityDecoratedPot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("sherds")) {
            this.sherds = new String[4];
            ListTag<StringTag> sherds = namedTag.getList("sherds", StringTag.class);
            for (int i = 0; i < 4; i++) {
                if (i >= sherds.size()) {
                    this.sherds[i] = ItemFullNames.BRICK;
                    continue;
                }
                this.sherds[i] = sherds.get(i).data;
            }
        } else {
            sherds = null;
        }

        if (namedTag.contains("item")) {
            item = NBTIO.getItemHelper(namedTag.getCompound("item"));
            if (item.isNull()) {
                item = null;
            }
        } else {
            item = null;
        }

        animation = namedTag.getByte("animation");

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (sherds != null) {
            ListTag<StringTag> sherds = new ListTag<>();
            for (String sherd : this.sherds) {
                sherds.addString(sherd);
            }
            namedTag.putList("sherds", sherds);
        } else {
            namedTag.remove("sherds");
        }

        namedTag.putCompound("item", NBTIO.putItemHelper(item));

        namedTag.putByte("animation", animation);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.DECORATED_POT;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, DECORATED_POT)
                .putCompound("item", NBTIO.putItemHelper(item))
                .putByte("animation", animation);

        if (sherds != null) {
            ListTag<StringTag> sherds = new ListTag<>();
            for (String sherd : this.sherds) {
                sherds.addString(sherd);
            }
            nbt.putList("sherds", sherds);
        }

        return nbt;
    }

    @Override
    public void onBreak() {
        if (item == null) {
            return;
        }
        level.dropItem(this, item);
        item = null;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    public void setItem(@Nullable Item item) {
        this.item = item;
    }

    @Nullable
    public String[] getSherds() {
        return sherds;
    }

    public void setSherds(String sherdA, String sherdB, String sherdC, String sherdD) {
        sherds = new String[4];
        sherds[0] = sherdA;
        sherds[1] = sherdB;
        sherds[2] = sherdC;
        sherds[3] = sherdD;
    }

    public void clearSherds() {
        this.sherds = null;
    }

    public void playAnimation(int animation) {
        this.animation = animation;
        spawnToAll();
        this.animation = ANIMATION_NONE;
    }
}
