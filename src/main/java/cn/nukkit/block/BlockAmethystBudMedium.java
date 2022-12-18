package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;

public class BlockAmethystBudMedium extends BlockAmethystBud {
    public BlockAmethystBudMedium() {
        this(0);
    }

    public BlockAmethystBudMedium(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MEDIUM_AMETHYST_BUD;
    }

    @Override
    public String getName() {
        return "Medium  Amethyst Bud";
    }

    @Override
    public int getLightLevel() {
        return 2;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getBlockFace()) {
            case DOWN:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y + 12.0 / 16, this.z + 3.0 / 16, this.x + 1 - 3.0 / 16, this.y + 1, this.z + 1 - 3.0 / 16);
            default:
            case UP:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y, this.z + 3.0 / 16, this.x + 1 - 3.0 / 16, this.y + 1 - 12.0 / 16, this.z + 1 - 3.0 / 16);
            case NORTH:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y + 3.0 / 16, this.z + 12.0 / 16, this.x + 1 - 3.0 / 16, this.y + 1 - 3.0 / 16, this.z + 1);
            case SOUTH:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y + 3.0 / 16, this.z, this.x + 1 - 3.0 / 16, this.y + 1 - 3.0 / 16, this.z + 1 - 12.0 / 16);
            case WEST:
                return new SimpleAxisAlignedBB(this.x + 12.0 / 16, this.y + 3.0 / 16, this.z + 3.0 / 16, this.x + 1, this.y + 1 - 3.0 / 16, this.z + 1 - 3.0 / 16);
            case EAST:
                return new SimpleAxisAlignedBB(this.x, this.y + 3.0 / 16, this.z + 3.0 / 16, this.x + 1 - 12.0 / 16, this.y + 1 - 3.0 / 16, this.z + 1 - 3.0 / 16);
        }
    }

    @Override
    protected int getNextStageBlockId() {
        return LARGE_AMETHYST_BUD;
    }
}
