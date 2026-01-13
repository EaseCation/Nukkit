package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemBoard extends Item {
    public static final int SLATE = 0;
    public static final int BOARD = 1;
    public static final int POSTER = 2;

    private static final String[] CHALKBOARD_TYPE_NAMES = {
            "oneByOne",
            "twoByOne",
            "threeByTwo",
    };

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
    public String getDescriptionId() {
        int type = getDamage();
        if (type >= 0 && type < CHALKBOARD_TYPE_NAMES.length) {
            return "tile.chalkboard." + CHALKBOARD_TYPE_NAMES[type] + ".name";
        }
        return "tile.chalkboard.oneByOne.name";
    }

    @Override
    public boolean isStackedByData() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
