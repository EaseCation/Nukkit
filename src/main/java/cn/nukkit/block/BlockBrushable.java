package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBrushableBlock;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public abstract class BlockBrushable extends BlockFallable {
    public static final int HANGING_BIT = 0b1;
    public static final int BRUSHED_PROGRESS_MASK = 0b110;
    public static final int BRUSHED_PROGRESS_START = 1;

    public BlockBrushable() {
        this(0);
    }

    public BlockBrushable(int meta) {
        super(meta);
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BRUSHABLE_BLOCK;
    }

    @Override
    public float getHardness() {
        return 0.25f;
    }

    @Override
    public float getResistance() {
        return 1.25f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            return BlockSeagrass.trySpawnSeaGrass(this, item, player);
        }

        if (item.is(Item.BRUSH)) {
            BlockEntityBrushableBlock blockEntity = getBlockEntity();
            if (blockEntity == null) {
                blockEntity = createBlockEntity(null);
                if (blockEntity == null) {
                    return true;
                }
            }

            if (blockEntity.brush(this, face) && player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

    protected BlockEntityBrushableBlock createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BRUSHABLE_BLOCK);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityBrushableBlock) BlockEntities.createBlockEntity(BlockEntityType.BRUSHABLE_BLOCK, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityBrushableBlock getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityBrushableBlock blockEntity) {
            return blockEntity;
        }
        return null;
    }

    public abstract int getBrushedBlockId();

    public int getBrushedProgress() {
        return (getDamage() & BRUSHED_PROGRESS_MASK) >> BRUSHED_PROGRESS_START;
    }

    public void setBrushedProgress(int progress) {
        setDamage((getDamage() & ~BRUSHED_PROGRESS_MASK) | (progress << BRUSHED_PROGRESS_START));
    }
}
