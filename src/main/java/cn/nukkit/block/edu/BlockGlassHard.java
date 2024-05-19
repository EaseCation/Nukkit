package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlass;
import cn.nukkit.item.Item;

public class BlockGlassHard extends BlockGlass {

    public BlockGlassHard() {
    }

    @Override
    public int getId() {
        return HARD_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Glass";
    }

    @Override
    public float getHardness() {
        return 10;
    }

    @Override
    public float getResistance() {
        return 50;
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                this.toItem(true)
        };
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
