package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockNetherWartBlock extends BlockSolid {

    public BlockNetherWartBlock() {
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

    @Override
    public int getId() {
        return NETHER_WART_BLOCK;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem(true)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
