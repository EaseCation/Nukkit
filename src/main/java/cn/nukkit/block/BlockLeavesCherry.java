package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockLeavesCherry extends BlockLeaves {
    BlockLeavesCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_LEAVES;
    }

    public String getName() {
        return "Cherry Leaves";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    protected Item getSapling() {
        return Item.get(ItemBlockID.CHERRY_SAPLING);
    }
}
