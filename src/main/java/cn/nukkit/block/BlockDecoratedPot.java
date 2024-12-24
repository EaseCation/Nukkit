package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDecoratedPot;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.V1_20_50;

public class BlockDecoratedPot extends BlockTransparent {
    public static final int DIRECTION_MASK = 0b11;

    public BlockDecoratedPot() {
        this(0);
    }

    public BlockDecoratedPot(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DECORATED_POT;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.DECORATED_POT;
    }

    @Override
    public String getName() {
        return "Decorated Pot";
    }

    @Override
    public float getHardness() {
        return 0f;
    }

    @Override
    public float getResistance() {
        return 0f;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
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
        return V1_20_50.isAvailable();
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }

        BlockEntityDecoratedPot blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return true;
            }
        }

        boolean success = false;
        Item slot = blockEntity.getItem();
        if (slot == null) {
            if (!item.isNull()) {
                success = true;

                slot = item.clone();
                slot.setCount(1);

                if (player.isSurvivalLike()) {
                    item.pop();
                }
            }
        } else if (slot.getCount() < slot.getMaxStackSize() && slot.equals(item)) {
            success = true;

            slot.grow(1);

            if (player.isSurvivalLike()) {
                item.pop();
            }
        }

        if (success) {
            blockEntity.setItem(slot);

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_DECORATED_POT_INSERT);
            blockEntity.playAnimation(BlockEntityDecoratedPot.ANIMATION_INSERT_SUCCESS);
        } else {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_DECORATED_POT_INSERT_FAILED);
            blockEntity.playAnimation(BlockEntityDecoratedPot.ANIMATION_INSERT_FAIL);
        }
        return true;
    }

    @Override
    public void onProjectileHit(EntityProjectile projectile, MovingObjectPosition hitResult) {
        level.useBreakOn(this, true);
    }

    protected BlockEntityDecoratedPot createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.DECORATED_POT);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityDecoratedPot) BlockEntities.createBlockEntity(BlockEntityType.DECORATED_POT, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityDecoratedPot getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityDecoratedPot blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
