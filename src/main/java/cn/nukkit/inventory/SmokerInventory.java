package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntitySmoker;

public class SmokerInventory extends FurnaceInventory {

    public SmokerInventory(BlockEntitySmoker furnace) {
        super(furnace, InventoryType.SMOKER);
    }

    @Override
    public BlockEntitySmoker getHolder() {
        return (BlockEntitySmoker) this.holder;
    }
}
