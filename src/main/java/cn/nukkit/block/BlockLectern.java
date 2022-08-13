package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritable;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockLectern extends BlockTransparentMeta implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int POWERED_BIT = 0b100;

    public BlockLectern() {
        this(0);
    }

    public BlockLectern(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LECTERN;
    }

    @Override
    public String getName() {
        return "Lectern";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.LECTERN;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return y + 1 - 1 / 16.0;
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
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId());
        if (addUserData) {
            BlockEntityLectern blockEntity = getBlockEntity();
            if (blockEntity != null) {
                item.setCustomName(blockEntity.getName());
                item.setRepairCost(blockEntity.getRepairCost());
            }
        }
        return item;
    }

    @Override
    public Item[] getDrops(Item item) {
        Item drop = toItem(true);
        BlockEntityLectern blockEntity = getBlockEntity();
        ItemBookWritable book = blockEntity.getBook();
        return book != null ? new Item[]{
                book.clone(),
                drop
        } : new Item[]{drop};
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex());
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
        BlockEntityLectern blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        if (blockEntity.getBook() != null) {
            return true;
        }

        if (!(item instanceof ItemBookWritable)) {
            return true;
        }

        ItemBookWritable book = (ItemBookWritable) item.clone();
        book.setCount(1);
        blockEntity.setBook(book);

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_BOOK_PUT);

        if (player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isPowered()) {
                return 0;
            }

            setPowered(false);
            level.setBlock(this, this, true, false);

            if (level.isRedstoneEnabled()) {
                level.updateAroundRedstone(this, null);
                level.updateAroundRedstone(downVec(), null);
            }

            return type;
        }
        return 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowered() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.UP ? getWeakPower(side) : 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityLectern blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return 0;
        }
        ItemBookWritable book = blockEntity.getBook();
        if (book == null) {
            return 0;
        }
        int totalPages = book.getTotalPages();
        if (totalPages == 0) {
            return 15;
        }
        return blockEntity.getPage() / totalPages * 15;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    protected BlockEntityLectern createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.LECTERN);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityLectern) BlockEntity.createBlockEntity(BlockEntity.LECTERN, getChunk(), nbt);
    }

    protected BlockEntityLectern getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityLectern) {
            return (BlockEntityLectern) blockEntity;
        }
        return null;
    }

    public boolean isPowered() {
        return (getDamage() & POWERED_BIT) == POWERED_BIT;
    }

    public void setPowered(boolean powered) {
        setDamage(powered ? getDamage() | POWERED_BIT : getDamage() & ~POWERED_BIT);
    }

    public void onPageTurn() {
        if (isPowered()) {
            return;
        }

        level.scheduleUpdate(this, 1);

        setPowered(true);
        level.setBlock(this, this, true, false);

        if (!level.isRedstoneEnabled()) {
            return;
        }
        level.updateAroundRedstone(this, null);
        level.updateAroundRedstone(downVec(), null);
    }
}
