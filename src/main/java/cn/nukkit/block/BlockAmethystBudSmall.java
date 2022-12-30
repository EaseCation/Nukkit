package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;

public class BlockAmethystBudSmall extends BlockAmethystBud {
    public BlockAmethystBudSmall() {
        this(0);
    }

    public BlockAmethystBudSmall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SMALL_AMETHYST_BUD;
    }

    @Override
    public String getName() {
        return "Small Amethyst Bud";
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getBlockFace()) {
            case DOWN:
                return new SimpleAxisAlignedBB(this.x + 4.0 / 16, this.y + 13.0 / 16, this.z + 4.0 / 16, this.x + 1 - 4.0 / 16, this.y + 1, this.z + 1 - 4.0 / 16);
            default:
            case UP:
                return new SimpleAxisAlignedBB(this.x + 4.0 / 16, this.y, this.z + 4.0 / 16, this.x + 1 - 4.0 / 16, this.y + 1 - 13.0 / 16, this.z + 1 - 4.0 / 16);
            case NORTH:
                return new SimpleAxisAlignedBB(this.x + 4.0 / 16, this.y + 4.0 / 16, this.z + 13.0 / 16, this.x + 1 - 4.0 / 16, this.y + 1 - 4.0 / 16, this.z + 1);
            case SOUTH:
                return new SimpleAxisAlignedBB(this.x + 4.0 / 16, this.y + 4.0 / 16, this.z, this.x + 1 - 4.0 / 16, this.y + 1 - 4.0 / 16, this.z + 1 - 13.0 / 16);
            case WEST:
                return new SimpleAxisAlignedBB(this.x + 13.0 / 16, this.y + 4.0 / 16, this.z + 4.0 / 16, this.x + 1, this.y + 1 - 4.0 / 16, this.z + 1 - 4.0 / 16);
            case EAST:
                return new SimpleAxisAlignedBB(this.x, this.y + 4.0 / 16, this.z + 4.0 / 16, this.x + 1 - 13.0 / 16, this.y + 1 - 4.0 / 16, this.z + 1 - 4.0 / 16);
        }
    }
}
