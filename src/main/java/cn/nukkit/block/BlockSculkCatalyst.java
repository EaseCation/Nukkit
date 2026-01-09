package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkCatalyst;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSculkCatalyst extends BlockSolid {
    public static final int BLOOM_BIT = 0b1;

    BlockSculkCatalyst() {

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
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getLightLevel() {
        return 6;
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

        return (BlockEntitySculkCatalyst) BlockEntities.createBlockEntity(BlockEntityType.SCULK_CATALYST, getChunk(), nbt);
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
