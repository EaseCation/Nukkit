package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateBirch extends BlockFenceGate {
    public BlockFenceGateBirch() {
        this(0);
    }

    public BlockFenceGateBirch(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Birch Fence Gate";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BIRCH_FENCE_GATE, 0, 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
