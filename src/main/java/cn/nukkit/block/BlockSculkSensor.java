package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkSensor;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSculkSensor extends BlockTransparentMeta {
    public static final int POWERED_BIT = 0b1;

    public BlockSculkSensor() {
        this(0);
    }

    public BlockSculkSensor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SCULK_SENSOR;
    }

    @Override
    public String getName() {
        return "Sculk Sensor";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SCULK_SENSOR;
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 7.5f;
    }

    @Override
    public int getLightLevel() {
        return 1;
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

    /* //TODO: redstone
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
        return getWeakPower(side);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return isPowered() ? 1 : 0;
    }
    */

    protected BlockEntitySculkSensor createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SCULK_SENSOR);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntitySculkSensor) BlockEntities.createBlockEntity(BlockEntityType.SCULK_SENSOR, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntitySculkSensor getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntitySculkSensor) {
            return (BlockEntitySculkSensor) blockEntity;
        }
        return null;
    }

    public boolean isPowered() {
        return getDamage() == POWERED_BIT;
    }

    public void setPowered(boolean powered) {
        setDamage(powered ? POWERED_BIT : 0);
    }
}
