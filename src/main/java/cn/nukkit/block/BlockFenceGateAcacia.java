package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateAcacia extends BlockFenceGate {
    public BlockFenceGateAcacia() {
        this(0);
    }

    public BlockFenceGateAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Acacia Fence Gate";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.ACACIA_FENCE_GATE, 0, 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
