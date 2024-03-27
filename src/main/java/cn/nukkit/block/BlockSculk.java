package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockSculk extends BlockSolid {
    public BlockSculk() {
    }

    @Override
    public int getId() {
        return SCULK;
    }

    @Override
    public String getName() {
        return "Sculk";
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 0.2f;
        }
        return 0.6f;
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
    public int getDropExp() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SCULK_BLOCK_COLOR;
    }
}
