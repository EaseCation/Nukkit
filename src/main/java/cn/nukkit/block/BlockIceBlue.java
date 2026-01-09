package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockIceBlue extends BlockSolid {

    BlockIceBlue() {

    }

    @Override
    public int getId() {
        return BLUE_ICE;
    }

    @Override
    public String getName() {
        return "Blue Ice";
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public float getHardness() {
        return 2.8f;
    }

    @Override
    public float getResistance() {
        return 14;
    }

    @Override
    public float getFrictionFactor() {
        return 0.99f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }
}
