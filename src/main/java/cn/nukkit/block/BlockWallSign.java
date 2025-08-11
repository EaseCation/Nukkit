package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockWallSign extends BlockSignPost {
    private static final AxisAlignedBB[] SELECTION_BOXES = {
            box(0, 4, 0, 16, 12, 2),
            box(0, 4, 0, 16, 12, 2),
            box(0, 4, 14, 16, 12, 16),
            box(0, 4, 0, 16, 12, 2),
            box(14, 4, 0, 16, 12, 16),
            box(0, 4, 0, 2, 12, 16),
    };

    private static final Vector3[] CENTERS = new Vector3[SELECTION_BOXES.length];

    static {
        for (int i = 0; i < SELECTION_BOXES.length; i++) {
            CENTERS[i] = SELECTION_BOXES[i].getCenter();
        }
    }

    public BlockWallSign() {
        this(0);
    }

    public BlockWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Oak Wall Sign";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getBlockFace().getOpposite()).canBeFlowedInto()) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return null; //TODO
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & 0x7);
    }

    @Override
    public boolean isStandingSign() {
        return false;
    }

    @Override
    public boolean isWallSign() {
        return true;
    }

    @Override
    protected float getRotationDegrees() {
        BlockFace face = getBlockFace();
        if (face.isVertical()) {
            face = BlockFace.SOUTH;
        }
        return face.getHorizontalAngle();
    }

    @Override
    protected Vector3 getSignCenter() {
        return CENTERS[getDamage()];
    }
}
