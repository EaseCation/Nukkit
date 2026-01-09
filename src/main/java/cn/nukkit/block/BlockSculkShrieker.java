package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkShrieker;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSculkShrieker extends BlockTransparent {
    public static final int ACTIVE_BIT = 0b1;
    public static final int CAN_SUMMON_BIT = 0b10;

    BlockSculkShrieker() {

    }

    @Override
    public int getId() {
        return SCULK_SHRIEKER;
    }

    @Override
    public String getName() {
        return "Sculk Shrieker";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SCULK_SHRIEKER;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public int getDropExp() {
        return 5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return y + 0.5;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 9 / 16f, z + 1);
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN;
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
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SCULK_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isActive()) {
                return 0;
            }

            setActive(false);
            level.setBlock(this, this, true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (isActive()) {
            return;
        }

        level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_SCULK_SHRIEK, new CompoundTag()
                .putInt("originX", getFloorX())
                .putInt("originY", getFloorY())
                .putInt("originZ", getFloorZ()));
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SCULK_SHRIEKER_SHRIEK);

        setActive(true);
        level.setBlock(this, this, true);

        level.scheduleUpdate(this, 90);
    }

    protected BlockEntitySculkShrieker createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SCULK_SHRIEKER);

        return (BlockEntitySculkShrieker) BlockEntities.createBlockEntity(BlockEntityType.SCULK_SHRIEKER, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntitySculkShrieker getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntitySculkShrieker) {
            return (BlockEntitySculkShrieker) blockEntity;
        }
        return null;
    }

    public boolean isActive() {
        return (getDamage() & ACTIVE_BIT) == ACTIVE_BIT;
    }

    public void setActive(boolean active) {
        setDamage(active ? getDamage() | ACTIVE_BIT : getDamage() & ~ACTIVE_BIT);
    }

    public boolean canSummon() {
        return (getDamage() & CAN_SUMMON_BIT) == CAN_SUMMON_BIT;
    }

    public void setSummon(boolean summon) {
        setDamage(summon ? getDamage() | CAN_SUMMON_BIT : getDamage() & ~CAN_SUMMON_BIT);
    }
}
