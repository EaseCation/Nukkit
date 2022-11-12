package cn.nukkit.block.edu;

import cn.nukkit.block.BlockGlassPane;
import cn.nukkit.item.Item;

public class BlockGlassPaneHard extends BlockGlassPane {

    public BlockGlassPaneHard() {
    }

    @Override
    public int getId() {
        return HARD_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Glass Pane";
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
