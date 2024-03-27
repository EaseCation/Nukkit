package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockSculkVein extends BlockMultiface {
    public BlockSculkVein() {
        this(0);
    }

    public BlockSculkVein(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SCULK_VEIN;
    }

    @Override
    public String getName() {
        return "Sculk Vein";
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SCULK_BLOCK_COLOR;
    }
}
