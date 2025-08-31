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
        Item item = Item.get(getItemId(SMOKER));
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
    public String getBlockEntityId() {
        return BlockEntity.SMOKER;
    }
}
