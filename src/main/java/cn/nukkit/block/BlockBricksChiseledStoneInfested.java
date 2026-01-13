package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockBricksChiseledStoneInfested extends BlockMonsterEgg {
    BlockBricksChiseledStoneInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_CHISELED_STONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Infested Chiseled Stone Bricks";
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.CHISELED_STONE_BRICKS),
        };
    }

    @Override
    public String getDescriptionId() {
        return "tile.monster_egg.chiseledbrick.name";
    }
}
