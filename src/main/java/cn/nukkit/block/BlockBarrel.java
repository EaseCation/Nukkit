package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockBarrel extends BlockSolidMeta implements Faceable {

    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int OPEN_BIT = 0b1000;

    public BlockBarrel() {
        this(0);
    }

    public BlockBarrel(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BARREL;
    }

    @Override
    public String getName() {
        return "Barrel";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BARREL;
    }

    @Override
    public float getHardness() {
        return 2.5f;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId());
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
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityBarrel blockEntity = this.getBlockEntity();
        if (blockEntity == null) {
            return 0;
        }
        return ContainerInventory.calculateRedstone(blockEntity.getInventory());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.x - x) < 2 && Math.abs(player.z - z) < 2) {
                double eyeY = player.y + player.getEyeHeight();
                if (eyeY - y > 2) {
                    setDamage(BlockFace.UP.getIndex());
                } else if (y - eyeY > 0) {
                    setDamage(BlockFace.DOWN.getIndex());
                } else {
                    setDamage(player.getHorizontalFacing().getOpposite().getIndex());
                }
            } else {
                setDamage(player.getHorizontalFacing().getOpposite().getIndex());
            }
        }

        level.setBlock(block, this, true);
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

        BlockEntityBarrel blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        player.addWindow(blockEntity.getInventory());
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & FACING_DIRECTION_MASK);
    }

    protected BlockEntityBarrel createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BARREL);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityBarrel) BlockEntities.createBlockEntity(BlockEntityType.BARREL, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityBarrel getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityBarrel)) {
            return null;
        }
        return (BlockEntityBarrel) blockEntity;
    }

    public boolean isOpen() {
        return (getDamage() & OPEN_BIT) == OPEN_BIT;
    }

    public void setOpen(boolean open) {
        setDamage(open ? getDamage() | OPEN_BIT : getDamage() & FACING_DIRECTION_MASK);
    }
}
