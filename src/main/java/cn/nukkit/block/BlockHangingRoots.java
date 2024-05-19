package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockHangingRoots extends BlockFlowable {
    public BlockHangingRoots() {
        this(0);
    }

    public BlockHangingRoots(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return HANGING_ROOTS;
    }

    @Override
    public String getName() {
        return "Hanging Roots";
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHEARS;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
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
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }

    private boolean canSurvive() {
        return SupportType.hasFullSupport(up(), BlockFace.DOWN);
    }
}
