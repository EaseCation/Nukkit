package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;

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
        return Item.get(getItemId(SMOKER));
    }

    @Override
    public String getBlockEntityId() {
        return BlockEntity.SMOKER;
    }
}
