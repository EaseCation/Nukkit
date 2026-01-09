package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFroglightVerdant extends BlockFroglight {
    BlockFroglightVerdant() {

    }

    @Override
    public int getId() {
        return VERDANT_FROGLIGHT;
    }

    @Override
    public String getName() {
        return "Verdant Froglight";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GLOW_LICHEN_BLOCK_COLOR;
    }
}
