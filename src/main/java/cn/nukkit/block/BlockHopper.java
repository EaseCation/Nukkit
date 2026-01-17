package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 */
public class BlockHopper extends BlockTransparent implements Faceable {
    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int TOGGLE_BIT = 0b1000;

    BlockHopper() {

    }

    @Override
    public int getId() {
        return HOPPER;
    }

    @Override
    public String getName() {
        return "Hopper Block";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.HOPPER;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 24;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockFace facing = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }

        this.setDamage(facing.getIndex());

        if (this.level.isRedstoneEnabled()) {
            boolean powered = this.level.isBlockPowered(this);

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
            }
        }

        this.level.setBlock(this, this, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.HOPPER);

        CompoundTag itemNbt = item.getNamedTag();
        nbt.putList(itemNbt != null ? itemNbt.getList("Items", CompoundTag.class) : new ListTag<>("Items"));

        BlockEntityHopper hopper = (BlockEntityHopper) BlockEntities.createBlockEntity(BlockEntityType.HOPPER, this.level.getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);
        return hopper != null;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntityHopper blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        blockEntity.unpackLootTable();
        return player.addWindow(blockEntity.getInventory()) != -1;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return ContainerInventory.calculateRedstone(((BlockEntityHopper) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    public boolean isEnabled() {
        return (this.getDamage() & TOGGLE_BIT) != TOGGLE_BIT;
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            this.setDamage(this.getDamage() ^ TOGGLE_BIT);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean powered = this.level.isBlockPowered(this);

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
                this.level.setBlock(this, this, true, false);

                if (!powered) {
                    BlockEntity be = this.level.getBlockEntity(this);

                    if (be != null) {
                        be.scheduleUpdate();
                    }
                }
            }

            return type;
        }

        return 0;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem(true)};
        }

        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.HOPPER);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & FACING_DIRECTION_MASK);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.UP || face == BlockFace.DOWN && type == SupportType.CENTER && getBlockFace() == BlockFace.DOWN;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB[] getCollisionShape(int flags) {
        return new AxisAlignedBB[]{
                new SimpleAxisAlignedBB(x, y, z, x + 1, y + 10 / 16f, z + 1), // bottom
                new SimpleAxisAlignedBB(x, y, z, x + 2 / 16f, y + 1, z + 1), // west
                new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 2 / 16f), // north
                new SimpleAxisAlignedBB(x + 1 - 2 / 16f, y, z, x + 1, y + 1, z + 1), // east
                new SimpleAxisAlignedBB(x, y, z + 1 - 2 / 16f, x + 1, y + 1, z + 1), // south
        };
    }

    protected BlockEntityHopper createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.HOPPER);

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

        return (BlockEntityHopper) BlockEntities.createBlockEntity(BlockEntityType.HOPPER, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityHopper getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityHopper blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
