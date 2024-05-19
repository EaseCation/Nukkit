package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockItemFrame extends BlockTransparentMeta implements Faceable {
    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int MAP_BIT = 0b1000;
    public static final int PHOTO_BIT = 0b10000;

    public static final int[] LEGACY_DIRECTION_BITS_TO_FACING_DIRECTION_BITS = {5, 4, 3, 2}; // 1.12 to 1.13

    public BlockItemFrame() {
        this(0);
    }

    public BlockItemFrame(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_FRAME;
    }

    @Override
    public String getName() {
        return "Item Frame";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.ITEM_FRAME;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block block = getSide(getBlockFace().getOpposite());
            if (block.canBeFlowedInto() || block.isItemFrame()) {
                this.level.useBreakOn(this, true);
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        BlockEntityItemFrame itemFrame = getBlockEntity();
        if (itemFrame == null) {
            return false;
        }

        if (itemFrame.getItem().isNull()) {
            if (item.hasLock()) {
                return true;
            }

            Item itemOnFrame = item.clone();

            if (player != null && player.isSurvivalLike()) {
                item.pop();
                player.getInventory().setItemInHand(item);
            }

            itemOnFrame.setCount(1);
            itemFrame.setItem(itemOnFrame);
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ADDED);
        } else {
            itemFrame.rotateItem();
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED);
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.canBeFlowedInto() && !target.isItemFrame()) {
            this.setDamage(face.getIndex());
            this.getLevel().setBlock(block, this, true, true);

            createBlockEntity(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_REMOVED);
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        BlockEntityItemFrame itemFrame = getBlockEntity();
        if (itemFrame != null && itemFrame.hasItem() && ThreadLocalRandom.current().nextFloat() <= itemFrame.getItemDropChance()) {
            return new Item[]{
                    toItem(true),
                    itemFrame.getItem(),
            };
        } else {
            return new Item[]{
                    toItem(true)
            };
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(Item.FRAME);
        if (addUserData) {
            BlockEntity blockEntity = getBlockEntity();
            if (blockEntity != null) {
                item.setCustomName(blockEntity.getName());
                item.setRepairCost(blockEntity.getRepairCost());
            }
        }
        return item;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityItemFrame blockEntity = getBlockEntity();

        if (blockEntity == null) {
            return 0;
        }

        return blockEntity.getAnalogOutput();
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
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & FACING_DIRECTION_MASK);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isItemFrame() {
        return true;
    }

    protected BlockEntityItemFrame createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, getBlockEntityId());

        if (item != null) {
            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }

            if (item.hasCustomBlockData()) {
                for (Tag tag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(tag.getName(), tag);
                }
            }
        }

        return (BlockEntityItemFrame) BlockEntities.createBlockEntity(getBlockEntityType(), getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityItemFrame getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityItemFrame) {
            return (BlockEntityItemFrame) blockEntity;
        }
        return null;
    }

    protected String getBlockEntityId() {
        return BlockEntity.ITEM_FRAME;
    }
}
