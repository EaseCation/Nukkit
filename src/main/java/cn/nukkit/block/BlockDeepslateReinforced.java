package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDeepslateReinforced extends BlockSolid {
    public BlockDeepslateReinforced() {
    }

    @Override
    public int getId() {
        return REINFORCED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Reinforced Deepslate";
    }

    @Override
    public float getHardness() {
        return 55;
    }

    @Override
    public float getResistance() {
        return 6000;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
