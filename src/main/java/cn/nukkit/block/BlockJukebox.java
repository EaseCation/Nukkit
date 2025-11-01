package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityJukebox;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockJukebox extends BlockSolid {

    public BlockJukebox() {
    }

    @Override
    public String getName() {
        return "Jukebox";
    }

    @Override
    public int getId() {
        return JUKEBOX;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.JUKEBOX;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityJukebox)) {
            blockEntity = this.createBlockEntity(item);
            if (blockEntity == null) {
                return true;
            }
        }

        BlockEntityJukebox jukebox = (BlockEntityJukebox) blockEntity;
        if (jukebox.getRecordItem() != null) {
            jukebox.dropItem();
        } else if (item instanceof ItemRecord) {
            jukebox.setRecordItem((ItemRecord) item);
            jukebox.play();

            if (level.isRedstoneEnabled()) {
                level.updateAroundRedstone(this, null);
            }

            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            createBlockEntity(item);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (super.onBreak(item, player)) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);

            if (blockEntity instanceof BlockEntityJukebox) {
                ((BlockEntityJukebox) blockEntity).dropItem();
            }
            return true;
        }

        return false;
    }

    private BlockEntity createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.JUKEBOX)
                .putList(new ListTag<>("Items"));

        return BlockEntities.createBlockEntity(BlockEntityType.JUKEBOX, getChunk(), nbt);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        BlockEntityJukebox blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return 0;
        }
        return blockEntity.isRecordPlaying() ? 15 : 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityJukebox blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return 0;
        }
        ItemRecord record = blockEntity.getRecordItem();
        if (record == null) {
            return 0;
        }
        return record.getComparatorSignal();
    }

    @Nullable
    protected BlockEntityJukebox getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityJukebox jukebox) {
            return jukebox;
        }
        return null;
    }
}
