package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySporeBlossom;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSporeBlossom extends BlockFlowable {
    public BlockSporeBlossom() {
        this(0);
    }

    public BlockSporeBlossom(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return SPORE_BLOSSOM;
    }

    @Override
    public String getName() {
        return "Spore Blossom";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SPORE_BLOSSOM;
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected BlockEntitySporeBlossom createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SPORE_BLOSSOM);

        return (BlockEntitySporeBlossom) BlockEntities.createBlockEntity(BlockEntityType.SPORE_BLOSSOM, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntitySporeBlossom getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntitySporeBlossom) {
            return (BlockEntitySporeBlossom) blockEntity;
        }
        return null;
    }

    private boolean canSurvive() {
        return SupportType.hasFullSupport(up(), BlockFace.DOWN);
    }
}
