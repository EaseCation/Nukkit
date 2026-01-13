package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockBricksMossyStoneInfested extends BlockMonsterEgg {
    BlockBricksMossyStoneInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_MOSSY_STONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Infested Mossy Stone Bricks";
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.MOSSY_STONE_BRICKS),
        };
    }

    @Override
    public String getDescriptionId() {
        return "tile.monster_egg.mossybrick.name";
    }
}
