package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class BlockHugeMushroomStem extends BlockHugeMushroom {
    public BlockHugeMushroomStem() {
        this(0);
    }

    public BlockHugeMushroomStem(int meta) {
        super(meta);
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
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), ALL_STEM);
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
