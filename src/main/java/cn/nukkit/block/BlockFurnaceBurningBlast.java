package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;

public class BlockFurnaceBurningBlast extends BlockFurnaceBurning {

    public BlockFurnaceBurningBlast() {
        this(0);
    }

    public BlockFurnaceBurningBlast(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_BLAST_FURNACE;
    }

    @Override
    public String getName() {
        return "Burning Blast Furnace";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BLAST_FURNACE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(getItemId(BLAST_FURNACE));
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
