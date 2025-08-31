package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShelf;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;

public abstract class BlockShelf extends BlockSolid implements Faceable {
    public static final int TYPE_MASK = 0b11;
    public static final int POWERED_BIT = 0b100;
    public static final int DIRECTION_MASK = 0b11000;
    public static final int DIRECTION_OFFSET = 3;

    public static final int TYPE_UNCONNECTED = 0;
    public static final int TYPE_LEFT = 1;
    public static final int TYPE_CENTER = 2;
    public static final int TYPE_RIGHT = 3;

    protected BlockShelf(int meta) {
        super(meta);
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SHELF;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
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
    public int getFuelTime() {
        return 300;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            setDamage((player.getHorizontalFacing().getOpposite().getHorizontalIndex() & DIRECTION_MASK) << DIRECTION_OFFSET);
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

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

        BlockEntityShelf blockEntity = getBlockEntity(true);
        if (blockEntity == null) {
            return true;
        }

        if (isPowered()) {
            Deque<BlockEntityShelf> shelves = new LinkedList<>();
            BlockFace facing = getBlockFace();
            int type = getType();
            if (type == TYPE_CENTER) {
                shelves.offer(blockEntity);
                if (getSide(facing.rotateY()) instanceof BlockShelf block && block.isPowered() && block.getType() == TYPE_RIGHT) {
                    shelves.offerFirst(block.getBlockEntity(true));
                }
                if (getSide(facing.rotateYCCW()) instanceof BlockShelf block && block.isPowered() && block.getType() == TYPE_LEFT) {
                    shelves.offerLast(block.getBlockEntity(true));
                }
            } else if (type == TYPE_LEFT) {
                shelves.offer(blockEntity);
                BlockFace side = facing.rotateY();
                if (getSide(side) instanceof BlockShelf block && block.isPowered()) {
                    int connection = block.getType();
                    if (connection == TYPE_CENTER) {
                        shelves.offerFirst(block.getBlockEntity(true));
                        if (block.getSide(side) instanceof BlockShelf edge && edge.isPowered() && edge.getType() == TYPE_RIGHT) {
                            shelves.offerFirst(edge.getBlockEntity(true));
                        }
                    } else if (connection == TYPE_RIGHT) {
                        shelves.offerFirst(block.getBlockEntity(true));
                    }
                }
            } else if (type == TYPE_RIGHT) {
                shelves.offer(blockEntity);
                BlockFace side = facing.rotateYCCW();
                if (getSide(side) instanceof BlockShelf block && block.isPowered()) {
                    int connection = block.getType();
                    if (connection == TYPE_CENTER) {
                        shelves.offerLast(block.getBlockEntity(true));
                        if (block.getSide(side) instanceof BlockShelf edge && edge.isPowered() && edge.getType() == TYPE_LEFT) {
                            shelves.offerLast(edge.getBlockEntity(true));
                        }
                    } else if (connection == TYPE_LEFT) {
                        shelves.offerLast(block.getBlockEntity(true));
                    }
                }
            }

            PlayerInventory inventory = player.getInventory();
            for (int slot = inventory.getHotbarSize() - 3; slot >= 0; slot -= 3) {
                BlockEntityShelf shelf = shelves.pollLast();
                if (shelf == null) {
                    break;
                }
                shelf.swapItems(inventory, slot);
                shelf.spawnToAll();
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_MULTI_ITEM_SWAP, getFullId());
            return true;
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
                return false;
        }
        int slot = faceX < 0.3125f ? 0 : faceX < 0.6875f ? 1 : 2;

        Item storedItem = blockEntity.getItem(slot);
        boolean emptyHand = item.isNull();
        if (storedItem == null && emptyHand) {
            return false;
        }

        blockEntity.setItem(slot, emptyHand ? null : item.clone());
        blockEntity.spawnToAll();

        if (player.isCreativeLike()) {
            storedItem = null;
        } else {
            PlayerInventory inventory = player.getInventory();
            inventory.setItem(inventory.getHeldItemIndex(), storedItem != null ? storedItem : Items.air());
        }

        if (storedItem != null && !emptyHand) {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SINGLE_ITEM_SWAP, getFullId());
        } else {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE_ITEM, getFullId());
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            boolean powered = level.isBlockPowered(this);
            if (powered) {
                if (isPowered()) {
                    return 0;
                }
                setPowered(true);

                BlockFace facing = getBlockFace();
                boolean left = false;
                if (getSide(facing.rotateY()) instanceof BlockShelf shelf && shelf.isPowered()) {
                    int leftType = shelf.getType();
                    if (leftType == TYPE_UNCONNECTED) {
                        left = true;
                        setType(TYPE_LEFT);
                        shelf.setType(TYPE_RIGHT);
                        level.setBlock(shelf, shelf, true, false);
                    } else if (leftType == TYPE_RIGHT) {
                        left = true;
                    }
                }
                boolean right = false;
                if (getSide(facing.rotateYCCW()) instanceof BlockShelf shelf && shelf.isPowered()) {
                    int rightType = shelf.getType();
                    if (rightType == TYPE_UNCONNECTED) {
                        right = true;
                        setType(TYPE_RIGHT);
                        shelf.setType(TYPE_LEFT);
                        level.setBlock(shelf, shelf, true, false);
                    } else if (rightType == TYPE_LEFT) {
                        right = true;
                    }
                }

                if (left && right) {
                    setType(TYPE_CENTER);
                }
            } else if (isPowered()) {
                setPowered(false);

                int connection = getType();
                BlockFace facing = getBlockFace();
                if ((connection == TYPE_CENTER || connection == TYPE_LEFT)
                        && getSide(facing.rotateY()) instanceof BlockShelf shelf && shelf.isPowered()) {
                    int leftType = shelf.getType();
                    if (leftType == TYPE_CENTER) {
                        shelf.setType(TYPE_LEFT);
                        level.setBlock(shelf, shelf, true, false);
                    } else if (leftType == TYPE_RIGHT) {
                        shelf.setType(TYPE_UNCONNECTED);
                        level.setBlock(shelf, shelf, true, false);
                    }
                }
                if ((connection == TYPE_CENTER || connection == TYPE_RIGHT)
                        && getSide(facing.rotateYCCW()) instanceof BlockShelf shelf && shelf.isPowered()) {
                    int rightType = shelf.getType();
                    if (rightType == TYPE_CENTER) {
                        shelf.setType(TYPE_RIGHT);
                        level.setBlock(shelf, shelf, true, false);
                    } else if (rightType == TYPE_LEFT) {
                        shelf.setType(TYPE_UNCONNECTED);
                        level.setBlock(shelf, shelf, true, false);
                    }
                }

                setType(TYPE_UNCONNECTED);
            } else {
                return 0;
            }
            level.setBlock(this, this, true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityShelf blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return 0;
        }
        return blockEntity.getComparatorSignal();
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return switch (getBlockFace()) {
            case WEST -> new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 5 / 16f);
            case NORTH -> new SimpleAxisAlignedBB(x + 1 - 5 / 16f, y, z, x + 1, y + 1, z + 1);
            case EAST -> new SimpleAxisAlignedBB(x, y, z + 1 - 5 / 16f, x + 1, y + 1, z + 1);
            default -> new SimpleAxisAlignedBB(x, y, z, x + 5 / 16f, y + 1, z + 1);
        };
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == getBlockFace().getOpposite();
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((getDamage() & DIRECTION_MASK) >> DIRECTION_OFFSET);
    }

    protected BlockEntityShelf createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SHELF);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityShelf) BlockEntities.createBlockEntity(BlockEntityType.SHELF, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityShelf getBlockEntity() {
        return getBlockEntity(false);
    }

    @Nullable
    protected BlockEntityShelf getBlockEntity(boolean create) {
        if (level != null && level.getBlockEntity(this) instanceof BlockEntityShelf blockEntity) {
            return blockEntity;
        }
        if (create) {
            return createBlockEntity(null);
        }
        return null;
    }

    public int getType() {
        return getDamage() & TYPE_MASK;
    }

    public void setType(int type) {
        setDamage(getDamage() & ~TYPE_MASK | type & TYPE_MASK);
    }

    public boolean isPowered() {
        return (getDamage() & POWERED_BIT) != 0;
    }

    public void setPowered(boolean powered) {
        setDamage(powered ? getDamage() | POWERED_BIT : getDamage() & ~POWERED_BIT);
    }
}
