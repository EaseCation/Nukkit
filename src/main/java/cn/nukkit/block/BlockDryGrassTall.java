package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class BlockDryGrassTall extends BlockDryGrassShort {
    public BlockDryGrassTall() {
    }

    @Override
    public int getId() {
        return TALL_DRY_GRASS;
    }

    @Override
    public String getName() {
        return "Tall Dry Grass";
    }

    @Override
    protected boolean onFertilize(Item item, Player player) {
        return onFertilizeSpread(item, player);
    }

    @Override
    protected void spread(Block block) {
        level.setBlock(block, get(SHORT_DRY_GRASS), true);
    }
}
