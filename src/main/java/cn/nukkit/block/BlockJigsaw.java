package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityJigsawBlock;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockJigsaw extends BlockSolidMeta implements Faceable {

    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int ROTATION_MASK = 0b11000;

    public BlockJigsaw() {
        this(0);
    }

    public BlockJigsaw(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JIGSAW;
    }

    @Override
    public String getName() {
        return "Jigsaw Block";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.JIGSAW_BLOCK;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
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
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (!player.isCreative()) {
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

            //TODO: rotation
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
        if (player == null || !player.isCreative() || !player.isOp()) {
            return true;
        }
        //TODO: UI
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & FACING_DIRECTION_MASK);
    }

    protected BlockEntityJigsawBlock createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.JIGSAW_BLOCK);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityJigsawBlock) BlockEntities.createBlockEntity(BlockEntityType.JIGSAW_BLOCK, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityJigsawBlock getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityJigsawBlock) {
            return (BlockEntityJigsawBlock) blockEntity;
        }
        return null;
    }
}
