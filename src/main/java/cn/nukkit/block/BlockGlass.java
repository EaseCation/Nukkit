package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockGlass extends BlockTransparent {

    public BlockGlass() {
        this(0);
    }

    public BlockGlass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLASS;
    }

    @Override
    public String getName() {
        return "Glass";
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
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    @Override
    public boolean isGlass() {
        return true;
    }
}
