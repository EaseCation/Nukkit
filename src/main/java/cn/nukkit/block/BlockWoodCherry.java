package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockWoodCherry extends BlockLogCherry {
    public static final int STRIPPED_BIT = 0b1; //TODO: 1.21.40 remove
    public static final int PILLAR_AXIS_Y = 0b000;
    public static final int PILLAR_AXIS_X = 0b010;
    public static final int PILLAR_AXIS_Z = 0b100;
    public static final int PILLAR_AXIS_MASK = PILLAR_AXIS_X | PILLAR_AXIS_Z;
    public static final int PILLAR_AXIS_OFFSET = 1;

    private static final int[] PILLAR_AXIS = {
            PILLAR_AXIS_X,
            PILLAR_AXIS_Y,
            PILLAR_AXIS_Z,
    };

    public BlockWoodCherry() {
        this(0);
    }

    public BlockWoodCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_WOOD;
    }

    @Override
    public String getName() {
        return "Cherry Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (V1_21_40.isAvailable()) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        setDamage(PILLAR_AXIS[face.getAxis().ordinal()]);
        return level.setBlock(this, this, true);
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_CHERRY_WOOD, V1_21_40.isAvailable() ? getDamage() : getDamage() >> PILLAR_AXIS_OFFSET);
    }
}
