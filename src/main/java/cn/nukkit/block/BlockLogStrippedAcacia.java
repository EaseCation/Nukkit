package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedAcacia extends BlockLogStripped {

    public BlockLogStrippedAcacia() {
        this(0);
    }

    public BlockLogStrippedAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Acacia Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
