package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockSmokerBurning extends BlockFurnaceBurning {

    public BlockSmokerBurning() {
        this(0);
    }

    public BlockSmokerBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_SMOKER;
    }

    @Override
    public String getName() {
        return "Burning Smoker";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SMOKER;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return new ItemBlock(Block.get(BlockID.SMOKER));
    }

    @Override
    public String getBlockEntityId() {
        return BlockEntity.SMOKER;
    }
}
