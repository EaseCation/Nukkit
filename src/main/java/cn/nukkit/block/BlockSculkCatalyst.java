package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkCatalyst;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSculkCatalyst extends BlockSolidMeta {
    public static final int BLOOM_BIT = 0b1;

    public BlockSculkCatalyst() {
        this(0);
    }

    public BlockSculkCatalyst(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SCULK_CATALYST;
    }

    @Override
    public String getName() {
        return "Sculk Catalyst";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SCULK_CATALYST;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public int getLightLevel() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public int getDropExp() {
        return 5;
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
        return BlockColor.SCULK_BLOCK_COLOR;
    }

    protected BlockEntitySculkCatalyst createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SCULK_CATALYST);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntitySculkCatalyst) BlockEntity.createBlockEntity(BlockEntity.SCULK_CATALYST, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntitySculkCatalyst getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntitySculkCatalyst) {
            return (BlockEntitySculkCatalyst) blockEntity;
        }
        return null;
    }

    public boolean isBloom() {
        return getDamage() == BLOOM_BIT;
    }

    public void setBloom(boolean bloom) {
        setDamage(bloom ? BLOOM_BIT : 0);
    }
}
