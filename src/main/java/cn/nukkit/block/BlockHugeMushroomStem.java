package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class BlockHugeMushroomStem extends BlockHugeMushroom {
    BlockHugeMushroomStem() {

    }

    @Override
    public String getName() {
        return "Mushroom Stem";
    }

    @Override
    public int getId() {
        return MUSHROOM_STEM;
    }

    @Override
    public int getBlockDefaultMeta() {
        return ALL_STEM;
    }

    @Override
    public int getItemSerializationMeta() {
        return getBlockDefaultMeta();
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    protected int getSmallMushroomId() {
        return AIR;
    }
}
