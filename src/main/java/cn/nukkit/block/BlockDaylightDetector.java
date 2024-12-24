package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDaylightDetector;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetector extends BlockTransparent {

    public BlockDaylightDetector() {
        this(0);
    }

    public BlockDaylightDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR;
    }

    @Override
    public String getName() {
        return "Daylight Detector";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.DAYLIGHT_DETECTOR;
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId(DAYLIGHT_DETECTOR));
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.625;
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
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return getDamage();
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return 0;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        level.setBlock(this, get(DAYLIGHT_DETECTOR_INVERTED), true);
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isDaylightDetector() {
        return true;
    }

    protected BlockEntityDaylightDetector createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.DAYLIGHT_DETECTOR);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityDaylightDetector) BlockEntities.createBlockEntity(BlockEntityType.DAYLIGHT_DETECTOR, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityDaylightDetector getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityDaylightDetector) {
            return (BlockEntityDaylightDetector) blockEntity;
        }
        return null;
    }

    //This function is a suggestion that can be renamed or deleted
    protected boolean invertDetect() {
        return false;
    }

    //todo redstone

}
