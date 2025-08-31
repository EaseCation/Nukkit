package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockCactusFlower extends BlockFlowable {
    public BlockCactusFlower() {
        super(0);
    }

    @Override
    public int getId() {
        return CACTUS_FLOWER;
    }

    @Override
    public String getName() {
        return "Cactus Flower";
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }
        if (!canSurvive()) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (canSurvive()) {
                return 0;
            }
            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return x + 1 / 16f;
    }

    @Override
    public double getMinZ() {
        return z + 1 / 16f;
    }

    @Override
    public double getMaxX() {
        return x + 1 - 1 / 16f;
    }

    @Override
    public double getMaxY() {
        return y + 1 - 2 / 16f;
    }

    @Override
    public double getMaxZ() {
        return z + 1 - 1 / 16f;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean canSurvive() {
        return SupportType.hasCenterSupport(down(), BlockFace.UP);
    }
}
