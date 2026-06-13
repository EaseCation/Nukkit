package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class BlockDirtCoarse extends BlockDirt {
    BlockDirtCoarse() {

    }

    @Override
    public int getId() {
        return COARSE_DIRT;
    }

    @Override
    public String getName() {
        return "Coarse Dirt";
    }

    @Override
    protected int getHoedBlockId() {
        return DIRT;
    }

    @Override
    public String getDescriptionId() {
        return "tile.dirt.coarse.name";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(COARSE_DIRT)
        };
    }
}
