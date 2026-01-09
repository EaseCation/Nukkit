package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockBricksCrackedStoneInfested extends BlockMonsterEgg {
    BlockBricksCrackedStoneInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_CRACKED_STONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Infested Cracked Stone Bricks";
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.CRACKED_STONE_BRICKS),
        };
    }
}
