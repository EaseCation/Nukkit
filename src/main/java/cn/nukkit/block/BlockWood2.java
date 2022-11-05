package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWood2 extends BlockWood {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public static final int TYPE_MASK = 0b1;
    public static final int PILLAR_X_AXIS_BIT = 0b10;
    public static final int PILLAR_Z_AXIS_BIT = 0b100;
    public static final int PILLAR_AXIS_MASK = PILLAR_X_AXIS_BIT | PILLAR_Z_AXIS_BIT;

    private static final String[] NAMES = new String[]{
            "Acacia Log",
            "Dark Oak Log",
    };

    private static final short[] FACES = new short[]{
            0,
            0,
            0b100,
            0b100,
            0b10,
            0b10
    };

    public BlockWood2() {
        this(0);
    }

    public BlockWood2(int meta) {
        super(meta & 0x7);
    }

    @Override
    public int getId() {
        return LOG2;
    }

    @Override
    public String getName() {
        return NAMES[this.getLogType()];
    }

    @Override
    public BlockColor getColor() {
        switch (getLogType()) {
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
        }
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    protected int getStrippedLogId() {
        switch (getLogType()) {
            case ACACIA:
                return STRIPPED_ACACIA_LOG;
            case DARK_OAK:
                return STRIPPED_DARK_OAK_LOG;
        }
        return STRIPPED_OAK_LOG;
    }

    @Override
    protected int getWoodMeta() {
        return 0b100 | getLogType();
    }

    @Override
    public int getLogType() {
        return getDamage() & TYPE_MASK;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(this.getLogType() | FACES[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }
}
