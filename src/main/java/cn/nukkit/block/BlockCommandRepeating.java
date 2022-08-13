package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCommandRepeating extends BlockCommand {

    public BlockCommandRepeating() {
        this(0);
    }

    public BlockCommandRepeating(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return REPEATING_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Repeating Command Block";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
