package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;


public class BlockSeaLantern extends BlockSolid {
    public BlockSeaLantern() {
    }

    @Override
    public String getName() {
        return "Sea Lantern";
    }

    @Override
    public int getId() {
        return SEA_LANTERN;
    }

    @Override
    public float getResistance() {
        return 1.5f;
    }

    @Override
    public float getHardness() {
        return 0.3f;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(Item.PRISMARINE_CRYSTALS, 0, ThreadLocalRandom.current().nextInt(2, 4))
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
