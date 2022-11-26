package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEndGateway;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

/**
 * @author PikyCZ
 */
public class BlockEndGateway extends BlockTransparent {

    public BlockEndGateway() {
    }

    @Override
    public String getName() {
        return "End Gateway";
    }

    @Override
    public int getId() {
        return END_GATEWAY;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.END_GATEWAY;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    public boolean isSolid() {
        return false;
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
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    protected BlockEntityEndGateway createBlockEntity(@Nullable Item item) {
        return (BlockEntityEndGateway) BlockEntity.createBlockEntity(BlockEntity.END_GATEWAY, getChunk(),
                BlockEntity.getDefaultCompound(this, BlockEntity.END_GATEWAY));
    }

    @Nullable
    protected BlockEntityEndGateway getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityEndGateway) {
            return (BlockEntityEndGateway) blockEntity;
        }
        return null;
    }
}
