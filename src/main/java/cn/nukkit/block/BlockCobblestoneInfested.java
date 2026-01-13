package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockCobblestoneInfested extends BlockMonsterEgg {
    BlockCobblestoneInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_COBBLESTONE;
    }

    @Override
    public String getName() {
        return "Infested Cobblestone";
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.COBBLESTONE),
        };
    }

    @Override
    public String getDescriptionId() {
        return "tile.monster_egg.cobble.name";
    }
}
