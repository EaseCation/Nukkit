package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlassStained;
import cn.nukkit.item.Item;

public class BlockGlassStainedHard extends BlockGlassStained {

    public BlockGlassStainedHard() {
        this(0);
    }

    public BlockGlassStainedHard(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return HARD_STAINED_GLASS;
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
