package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockBricksStoneInfested extends BlockMonsterEgg {
    BlockBricksStoneInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_STONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Infested Stone Bricks";
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.STONE_BRICKS),
        };
    }

    @Override
    public String getDescriptionId() {
        return "tile.monster_egg.brick.name";
    }
}
