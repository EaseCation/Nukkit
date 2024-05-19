package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockWaterLily extends BlockFlowable {

    public BlockWaterLily() {
        this(0);
    }

    public BlockWaterLily(int meta) {
        // Lily pad can't have meta. Also stops the server from throwing an exception with the block palette.
        super(0);
    }

    @Override
    public String getName() {
        return "Lily Pad";
    }

    @Override
    public int getId() {
        return WATERLILY;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.015625;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.isWaterSource() || !target.isAir() && target.canContainWater() && level.getExtraBlock(target).isWaterSource()) {
            Block up = target.up();
            if (up.getId() == Block.AIR) {
                return level.setBlock(up, this, true);
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = down();
            if (below.isWaterSource() || !below.isAir() && below.canContainWater() && level.getExtraBlock(below).isWaterSource()) {
                return 0;
            }
            this.getLevel().useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public int getFullId() {
        return this.getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {

    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
