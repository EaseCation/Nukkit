package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkShrieker;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSculkShrieker extends BlockTransparentMeta {
    public static final int ACTIVE_BIT = 0b1;
    public static final int CAN_SUMMON_BIT = 0b10;

    public BlockSculkShrieker() {
        this(0);
    }

    public BlockSculkShrieker(int meta) {
        super(meta);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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

    protected BlockEntitySculkShrieker createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SCULK_SHRIEKER);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

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
