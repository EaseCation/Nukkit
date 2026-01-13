package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockStoneInfested extends BlockMonsterEgg {
    BlockStoneInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_STONE;
    }

    @Override
    public String getName() {
        return "Infested Stone";
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.STONE),
        };
    }

    @Override
    public String getDescriptionId() {
        return "tile.monster_egg.stone.name";
    }
}
