package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemItemFrame;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockItemFrame extends BlockTransparentMeta implements Faceable {
    private static final int[] FACING = new int[]{4, 5, 3, 2, 1, 0}; // TODO when 1.13 support arrives, add UP/DOWN facings

    private static final int FACING_BITMASK = 0b0111;
    private static final int MAP_BIT = 0b1000;

    public static final int LEGACY_DIRECTION_MASK = 0b11;

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
            if (block.canBeFlowedInto() || block instanceof BlockItemFrame) {
                this.level.useBreakOn(this);
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
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityItemFrame)) {
            return false;
        }
        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
        if (itemFrame.getItem().getId() == Item.AIR) {
            Item itemOnFrame = item.clone();
            if (player != null && player.isSurvival()) {
                itemOnFrame.setCount(itemOnFrame.getCount() - 1);
                player.getInventory().setItemInHand(itemOnFrame);
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
        if (!target.canBeFlowedInto() && !(target instanceof BlockItemFrame)) {
            if (face.isVertical()) {
                //TODO
                return false;
            }

            this.setDamage(face.getReversedHorizontalIndex());
            this.getLevel().setBlock(block, this, true, true);

            CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.ITEM_FRAME)
                    .putByte("ItemRotation", 0)
                    .putFloat("ItemDropChance", 1.0f);
            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }
            BlockEntityItemFrame frame = (BlockEntityItemFrame) BlockEntity.createBlockEntity(BlockEntity.ITEM_FRAME, getChunk(), nbt);
            if (frame == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_REMOVED);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
        if (itemFrame != null && ThreadLocalRandom.current().nextFloat() <= itemFrame.getItemDropChance()) {
            return new Item[]{
                    toItem(true), itemFrame.getItem().clone()
            };
        } else {
            return new Item[]{
                    toItem(true)
            };
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return new ItemItemFrame();
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
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityItemFrame) {
            return ((BlockEntityItemFrame) blockEntity).getAnalogOutput();
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromReversedHorizontalIndex(this.getDamage() & LEGACY_DIRECTION_MASK);
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
}
