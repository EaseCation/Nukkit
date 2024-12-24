package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityCrafter;

public class CrafterInventory extends ContainerInventory {
    public CrafterInventory(BlockEntityCrafter blockEntity) {
        super(blockEntity, InventoryType.CRAFTER);
    }

    @Override
    public BlockEntityCrafter getHolder() {
        return (BlockEntityCrafter) super.getHolder();
    }
}
