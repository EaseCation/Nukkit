package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockWallSign extends BlockSignPost {

    private static final int[] FACES = {
            3,
            2,
            5,
            4,
    };

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
            if (this.getDamage() >= 2 && this.getDamage() <= 5) {
                if (this.getSide(BlockFace.fromIndex(FACES[this.getDamage() - 2])).canBeFlowedInto()) {
                    this.getLevel().useBreakOn(this, true);
                }
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean isStandingSign() {
        return false;
    }

    @Override
    public boolean isWallSign() {
        return true;
    }
}
