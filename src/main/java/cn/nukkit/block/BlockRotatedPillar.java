package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public abstract class BlockRotatedPillar extends BlockSolidMeta {
    public static final int PILLAR_AXIS_Y = 0b00;
    public static final int PILLAR_AXIS_X = 0b01;
    public static final int PILLAR_AXIS_Z = 0b10;

    private static final int[] PILLAR_AXIS = {
            PILLAR_AXIS_X,
            PILLAR_AXIS_Y,
            PILLAR_AXIS_Z,
    };

    protected BlockRotatedPillar(int meta) {
        super(meta);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        setDamage(PILLAR_AXIS[face.getAxis().ordinal()]);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }
}
