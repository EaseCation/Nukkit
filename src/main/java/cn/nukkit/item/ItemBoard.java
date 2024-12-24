package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemBoard extends Item {
    public static final int SLATE = 0;
    public static final int BOARD = 1;
    public static final int POSTER = 2;

    public ItemBoard() {
        this(0, 1);
    }

    public ItemBoard(Integer meta) {
        this(meta, 1);
    }

    public ItemBoard(Integer meta, int count) {
        super(BOARD, meta, count, "Board");
        this.block = Block.get(Block.CHALKBOARD);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
