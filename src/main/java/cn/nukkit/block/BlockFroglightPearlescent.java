package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFroglightPearlescent extends BlockFroglight {
    public BlockFroglightPearlescent() {
        this(0);
    }

    public BlockFroglightPearlescent(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PEARLESCENT_FROGLIGHT;
    }

    @Override
    public String getName() {
        return "Pearlescent Froglight";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }
}
