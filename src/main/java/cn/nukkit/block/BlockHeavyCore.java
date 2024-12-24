package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockHeavyCore extends BlockTransparent {
    public BlockHeavyCore() {
    }

    @Override
    public int getId() {
        return HEAVY_CORE;
    }

    @Override
    public String getName() {
        return "Heavy Core";
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.METAL_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMinX() {
        return this.y + 4 / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.y + 4 / 16.0;
    }

    @Override
    public double getMaxX() {
        return this.y + 1 - 4 / 16.0;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 8 / 16.0;
    }

    @Override
    public double getMaxZ() {
        return this.y + 1 - 4 / 16.0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }
}
