package cn.nukkit.block.edu;

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
    public double getHardness() {
        return 10;
    }

    @Override
    public double getResistance() {
        return 50;
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem(true)
        };
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
