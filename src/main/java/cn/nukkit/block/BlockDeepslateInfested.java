package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;

public class BlockDeepslateInfested extends BlockDeepslate {
    BlockDeepslateInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Infested Deepslate";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 3.75f;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public Item[] getSilkTouchResource() {
        return new Item[]{
                Item.get(ItemBlockID.DEEPSLATE),
        };
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public Instrument getInstrument() {
        return Instrument.FLUTE;
    }
}
