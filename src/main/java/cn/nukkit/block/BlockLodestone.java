package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLodestone;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

public class BlockLodestone extends BlockSolid {
    public BlockLodestone() {
    }

    @Override
    public int getId() {
        return LODESTONE;
    }

    @Override
    public String getName() {
        return "Lodestone";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.LODESTONE;
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 3.5f;
        }
        return 2;
    }

    @Override
    public float getResistance() {
        return 17.5f;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
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
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    protected BlockEntityLodestone createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.LODESTONE);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityLodestone) BlockEntities.createBlockEntity(BlockEntityType.LODESTONE, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityLodestone getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityLodestone) {
            return (BlockEntityLodestone) blockEntity;
        }
        return null;
    }
}
