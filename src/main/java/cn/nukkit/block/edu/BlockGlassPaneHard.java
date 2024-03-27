package cn.nukkit.block.edu;

import cn.nukkit.Player;
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
    public boolean isChemistryFeature() {
        return true;
    }
}
