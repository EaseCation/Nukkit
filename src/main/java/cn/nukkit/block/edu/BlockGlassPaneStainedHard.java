package cn.nukkit.block.edu;

import cn.nukkit.block.BlockGlassPaneStained;
import cn.nukkit.item.Item;

public class BlockGlassPaneStainedHard extends BlockGlassPaneStained {

    public BlockGlassPaneStainedHard() {
        this(0);
    }

    public BlockGlassPaneStainedHard(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return HARD_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened " + super.getName();
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
}
