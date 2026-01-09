package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockCommand extends BlockSolid implements Faceable {

    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int CONDITIONAL_BIT = 0b1000;

    BlockCommand() {

    }

    @Override
    public int getId() {
        return COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Command Block";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.COMMAND_BLOCK;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            if (!player.isCreative() || !player.isOp()) {
                return false;
            }

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
        if (player == null || !player.isCreative() || !player.isOp()) {
            return true;
        }
        //TODO: UI
        //player.openBlockEditor(getFloorX(), getFloorY(), getFloorZ(), ContainerType.COMMAND_BLOCK);
        return true;
    }

    @Override
    public boolean hasUI() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & FACING_DIRECTION_MASK);
    }

    protected BlockEntityCommandBlock createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.COMMAND_BLOCK);

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

        return (BlockEntityCommandBlock) BlockEntities.createBlockEntity(BlockEntityType.COMMAND_BLOCK, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityCommandBlock getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityCommandBlock) {
            return (BlockEntityCommandBlock) blockEntity;
        }
        return null;
    }

    public boolean isConditional() {
        return (getDamage() & CONDITIONAL_BIT) == CONDITIONAL_BIT;
    }

    public void setConditional(boolean conditional) {
        setDamage(conditional ? getDamage() | CONDITIONAL_BIT : getDamage() & ~CONDITIONAL_BIT);
    }
}
