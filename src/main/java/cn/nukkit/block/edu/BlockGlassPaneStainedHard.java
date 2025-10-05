package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlassPaneStained;
import cn.nukkit.item.Item;

import static cn.nukkit.GameVersion.*;

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
    public boolean isStackedByData() {
        return !V1_20_60.isAvailable();
    }

    @Override
    public String getName() {
        return "Hardened " + super.getName();
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
