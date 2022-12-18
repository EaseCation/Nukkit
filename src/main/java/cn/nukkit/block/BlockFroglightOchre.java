package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFroglightOchre extends BlockFroglight {
    public BlockFroglightOchre() {
        this(0);
    }

    public BlockFroglightOchre(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OCHRE_FROGLIGHT;
    }

    @Override
    public String getName() {
        return "Ochre Froglight";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
