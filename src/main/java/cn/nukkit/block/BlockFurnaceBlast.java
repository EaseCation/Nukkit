package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockFurnaceBlast extends BlockFurnace {

    public BlockFurnaceBlast() {
        this(0);
    }

    public BlockFurnaceBlast(int meta) {
        super(meta);
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
        return new ItemBlock(Block.get(BlockID.BLAST_FURNACE));
    }

    @Override
    public String getBlockEntityId() {
        return BlockEntity.BLAST_FURNACE;
    }
}
