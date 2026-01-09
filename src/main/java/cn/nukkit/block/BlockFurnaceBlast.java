package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockFurnaceBlast extends BlockFurnace {

    BlockFurnaceBlast() {

    }

    @Override
    public int getId() {
        return BLAST_FURNACE;
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BLAST_FURNACE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(ItemBlockID.BLAST_FURNACE);
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
        return BlockEntity.BLAST_FURNACE;
    }
}
