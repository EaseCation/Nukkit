package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFroglightOchre extends BlockFroglight {
    BlockFroglightOchre() {

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
