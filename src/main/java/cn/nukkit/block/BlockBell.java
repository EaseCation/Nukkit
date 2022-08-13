package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBell;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockBell extends BlockSolidMeta implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int DIRECTION_BITS = 2;
    public static final int ATTACHMENT_MASK = 0b1100;
    public static final int TOGGLE_BIT = 0b10000;

    public static final int ATTACHMENT_STANDING = 0b00;
    public static final int ATTACHMENT_HANGING = 0b01;
    public static final int ATTACHMENT_SIDE = 0b10;
    public static final int ATTACHMENT_MULTIPLE = 0b11;

    public BlockBell() {
        this(0);
    }

    public BlockBell(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bell";
    }

    @Override
    public int getId() {
        return BELL;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GOLD_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!canBeSupportedBy(target, face)) {
            return false;
        }

        switch (face) {
            case UP:
                setDamage((ATTACHMENT_STANDING << DIRECTION_BITS) | (player != null ?
                        player.getHorizontalFacing().getOpposite().getHorizontalIndex() : getDamage() & DIRECTION_MASK));
                break;
            case DOWN:
                setDamage((ATTACHMENT_HANGING << DIRECTION_BITS) | (player != null ?
                        player.getHorizontalFacing().getOpposite().getHorizontalIndex() : getDamage() & DIRECTION_MASK));
                break;
            default:
                setDamage(((canBeSupportedBy(getSide(face), face.getOpposite()) ?
                        ATTACHMENT_MULTIPLE : ATTACHMENT_SIDE) << DIRECTION_BITS) | face.getHorizontalIndex());
                break;
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (player == null) {
            return false;
        }

        BlockFace blockDirection = getBlockFace();
        BlockFace faceHit = player.getHorizontalFacing().getOpposite();
        switch (getAttachment()) {
            case ATTACHMENT_STANDING:
                if (blockDirection.getAxis() != faceHit.getAxis()) {
                    return true;
                }
                break;
            case ATTACHMENT_HANGING:
                break;
            case ATTACHMENT_SIDE:
            case ATTACHMENT_MULTIPLE:
                if (blockDirection.rotateY() != faceHit && blockDirection.rotateYCCW() != faceHit) {
                    return true;
                }
                break;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_BELL_HIT);

        BlockEntityBell blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }
        blockEntity.ring(faceHit);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int attachment = getAttachment();

            if (attachment == ATTACHMENT_STANDING) {
                if (SupportType.hasCenterSupport(down(), BlockFace.UP)) {
                    return 0;
                }
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE));
                return type;
            }
            if (attachment == ATTACHMENT_HANGING) {
                if (SupportType.hasCenterSupport(up(), BlockFace.DOWN)) {
                    return 0;
                }
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE));
                return type;
            }

            BlockFace face = getBlockFace();
            BlockFace opposite = face.getOpposite();
            if (attachment == ATTACHMENT_MULTIPLE && !SupportType.hasFullSupport(getSide(face), opposite)) {
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE));
                return type;
            }

            if (!SupportType.hasFullSupport(getSide(opposite), face)) {
                level.useBreakOn(this, Item.get(ItemID.WOODEN_PICKAXE));
                return type;
            }
        }
        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getAttachment()) {
            default:
            case ATTACHMENT_STANDING:
                switch (getBlockFace().getAxis()) {
                    default:
                    case X:
                        return new SimpleAxisAlignedBB(x + 4 / 16.0, y, z, x + 1 - 4 / 16.0, y + 1 - 3 / 16.0, z + 1);
                    case Z:
                        return new SimpleAxisAlignedBB(x, y, z + 4 / 16.0, x + 1, y + 1 - 3 / 16.0, z + 1 - 4 / 16.0);
                }
            case ATTACHMENT_HANGING:
                return new SimpleAxisAlignedBB(x + 4 / 16.0, y + 4 / 16.0, z + 4 / 16.0, x + 1 - 4 / 16.0, y + 1, z + 1 - 4 / 16.0);
            case ATTACHMENT_SIDE:
                switch (getBlockFace()) {
                    default:
                    case SOUTH:
                        return new SimpleAxisAlignedBB(x, y + 4 / 16.0, z + 4 / 16.0, x + 1 - 4 / 16.0, y + 1 - 1 / 16.0, z + 1 - 4 / 16.0);
                    case WEST:
                        return new SimpleAxisAlignedBB(x + 4 / 16.0, y + 4 / 16.0, z + 4 / 16.0, x + 1 - 4 / 16.0, y + 1 - 1 / 16.0, z + 1);
                    case NORTH:
                        return new SimpleAxisAlignedBB(x + 4 / 16.0, y + 4 / 16.0, z + 4 / 16.0, x + 1, y + 1 - 1 / 16.0, z + 1 - 4 / 16.0);
                    case EAST:
                        return new SimpleAxisAlignedBB(x + 4 / 16.0, y + 4 / 16.0, z, x + 1 - 4 / 16.0, y + 1 - 1 / 16.0, z + 1 - 4 / 16.0);
                }
            case ATTACHMENT_MULTIPLE:
                switch (getBlockFace().rotateY().getAxis()) {
                    default:
                    case X:
                        return new SimpleAxisAlignedBB(x + 4 / 16.0, y + 4 / 16.0, z, x + 1 - 4 / 16.0, y + 1, z + 1);
                    case Z:
                        return new SimpleAxisAlignedBB(x, y + 4 / 16.0, z + 4 / 16.0, x + 1, y + 1, z + 1 - 4 / 16.0);
                }
        }
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    private boolean canBeSupportedBy(Block block, BlockFace face) {
        if (face.getAxis() == Axis.Y) {
            return SupportType.hasCenterSupport(block, face);
        }
        return SupportType.hasFullSupport(block, face);
    }

    protected BlockEntityBell createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BELL);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityBell) BlockEntity.createBlockEntity(BlockEntity.BELL, getChunk(), nbt);
    }

    protected BlockEntityBell getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityBell) {
            return (BlockEntityBell) blockEntity;
        }
        return null;
    }

    public int getAttachment() {
        return (getDamage() & ATTACHMENT_MASK) >> DIRECTION_BITS;
    }
}
