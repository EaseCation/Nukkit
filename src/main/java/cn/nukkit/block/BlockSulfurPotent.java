package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityPotentSulfur;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

public class BlockSulfurPotent extends BlockSulfur {
    public static final int DRY = 0;
    public static final int WET = 1;
    public static final int DORMANT = 2;
    public static final int ERUPTING = 3;

    BlockSulfurPotent() {
    }

    @Override
    public int getId() {
        return POTENT_SULFUR;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.POTENT_SULFUR_BLOCK;
    }

    @Override
    public String getName() {
        return "Potent Sulfur";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        //TODO: add to ticking queue

        createBlockEntity(item);
        return true;
    }

    protected BlockEntityPotentSulfur createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.POTENT_SULFUR_BLOCK);

        return (BlockEntityPotentSulfur) BlockEntities.createBlockEntity(BlockEntityType.POTENT_SULFUR_BLOCK, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityPotentSulfur getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityPotentSulfur blockEntity) {
            return blockEntity;
        }
        return null;
    }
}
