package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChiseledBookshelf;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockBookshelfChiseled extends BlockSolid implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int BOOKS_STORED_MASK = 0b11111100;
    public static final int BOOKS_STORED_START = 2;

    BlockBookshelfChiseled() {

    }

    @Override
    public int getId() {
        return CHISELED_BOOKSHELF;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CHISELED_BOOKSHELF;
    }

    @Override
    public String getName() {
        return "Chiseled Bookshelf";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 7.5f;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
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
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex());
        }
        level.setBlock(this, this, true);
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }

        if (face != getBlockFace()) {
            return false;
        }

        BlockEntityChiseledBookshelf blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        float faceX;
        switch (face) {
            case SOUTH:
                faceX = fx;
                break;
            case WEST:
                faceX = fz;
                break;
            case NORTH:
                faceX = 1 - fx;
                break;
            case EAST:
                faceX = 1 - fz;
                break;
            default:
                return true;
        }
        int slot = faceX < 0.3125f ? 0 : faceX < 0.6875f ? 1 : 2;
        if (fy < 0.5f) {
            slot += 3;
        }

        Item book = blockEntity.getItem(slot);
        if (book != null) {
            blockEntity.setItem(slot, null);

            player.getInventory().addItemOrDrop(book);

            level.addLevelSoundEvent(this, book.is(Item.ENCHANTED_BOOK) ? LevelSoundEventPacket.SOUND_PICKUP_ENCHANTED : LevelSoundEventPacket.SOUND_PICKUP);
        } else if (!item.isBook()) {
            return true;
        } else {
            Item newItem = item.clone();
            newItem.setCount(1);

            if (!player.isCreativeLike()) {
                item.pop();
            }

            blockEntity.setItem(slot, newItem);

            level.addLevelSoundEvent(this, newItem.is(Item.ENCHANTED_BOOK) ? LevelSoundEventPacket.SOUND_INSERT_ENCHANTED : LevelSoundEventPacket.SOUND_INSERT);
        }

        int booksStored = 0;
        for (int i = 0; i < BlockEntityChiseledBookshelf.SLOT_COUNT; i++) {
            if (blockEntity.getItem(i) != null) {
                booksStored |= 1 << i;
            }
        }
        setDamage((booksStored << BOOKS_STORED_START) | (getDamage() & ~BOOKS_STORED_MASK));
        level.setBlock(this, this, true);
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityChiseledBookshelf blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return 0;
        }
        return blockEntity.getLastInteractedSlot() + 1;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    protected BlockEntityChiseledBookshelf createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CHISELED_BOOKSHELF);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityChiseledBookshelf) BlockEntities.createBlockEntity(BlockEntityType.CHISELED_BOOKSHELF, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityChiseledBookshelf getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityChiseledBookshelf blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
