package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class BlockHugeMushroomStem extends BlockHugeMushroom {
    BlockHugeMushroomStem() {

    }

    @Override
    public String getDescriptionId() {
        return "tile.brown_mushroom_block.stem.name";
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
    public int getItemKeepMetaMask() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDamage(getBlockDefaultMeta());
        return super.place(item, block, target, face, fx, fy, fz, player);
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
