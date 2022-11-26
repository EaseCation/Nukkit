package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceMangrove extends BlockFence {
    public BlockFenceMangrove() {
        super(0);
    }

    @Override
    public int getId() {
        return MANGROVE_FENCE;
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
