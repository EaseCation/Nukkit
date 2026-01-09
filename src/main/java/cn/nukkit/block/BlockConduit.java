package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityConduit;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockConduit extends BlockTransparent {

    BlockConduit() {

    }

    @Override
    public int getId() {
        return CONDUIT;
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CONDUIT;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
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
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMinX() {
        return this.y + 4 / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.y + 4 / 16.0;
    }

    @Override
    public double getMaxX() {
        return this.y + 1 - 4 / 16.0;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 8 / 16.0;
    }

    @Override
    public double getMaxZ() {
        return this.y + 1 - 4 / 16.0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
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
        createBlockEntity(item);
        return true;
    }

    protected BlockEntityConduit createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CONDUIT);

        return (BlockEntityConduit) BlockEntities.createBlockEntity(BlockEntityType.CONDUIT, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityConduit getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityConduit) {
            return (BlockEntityConduit) blockEntity;
        }
        return null;
    }
}
