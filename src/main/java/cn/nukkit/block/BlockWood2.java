package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public static final int TYPE_MASK = 0b1;

    private static final String[] NAMES = new String[]{
            "Acacia Log",
            "Dark Oak Log",
    };

    public BlockWood2() {
        this(0);
    }

    public BlockWood2(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LOG2;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & TYPE_MASK];
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & TYPE_MASK) {
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
        }
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    protected int getStrippedLogId() {
        switch (getDamage() & TYPE_MASK) {
            case ACACIA:
                return STRIPPED_ACACIA_LOG;
            case DARK_OAK:
                return STRIPPED_DARK_OAK_LOG;
        }
        return STRIPPED_OAK_LOG;
    }

    @Override
    protected int getWoodMeta() {
        return 0b100 | (getDamage() & TYPE_MASK);
    }
}
