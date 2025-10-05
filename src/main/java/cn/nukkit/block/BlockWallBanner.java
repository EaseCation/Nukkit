package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

/**
 * Created by PetteriM1
 */
public class BlockWallBanner extends BlockBanner {

    public BlockWallBanner() {
        this(0);
    }

    public BlockWallBanner(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_BANNER;
    }

    @Override
    public int getBlockDefaultMeta() {
        return 3;
    }

    @Override
    public String getName() {
        return "Wall Banner";
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
        return BlockFace.fromIndex(this.getDamage() & 0x7);
    }
}
