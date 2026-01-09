package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public abstract class BlockLight extends BlockTransparent {
    public static final int[] LIGHT_BLOCKS = {
            LIGHT_BLOCK_0,
            LIGHT_BLOCK_1,
            LIGHT_BLOCK_2,
            LIGHT_BLOCK_3,
            LIGHT_BLOCK_4,
            LIGHT_BLOCK_5,
            LIGHT_BLOCK_6,
            LIGHT_BLOCK_7,
            LIGHT_BLOCK_8,
            LIGHT_BLOCK_9,
            LIGHT_BLOCK_10,
            LIGHT_BLOCK_11,
            LIGHT_BLOCK_12,
            LIGHT_BLOCK_13,
            LIGHT_BLOCK_14,
            LIGHT_BLOCK_15,
    };

    @Override
    public String getName() {
        return "Light";
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getResistance() {
        return 18000004;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return this;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null && !player.isCreative() || item.getId() != getItemId()) {
            return false;
        }

        level.setBlock(this, get(getNextLightLevelBlockId()), true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Items.air();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isLightBlock() {
        return true;
    }

    @Override
    public abstract int getLightLevel();

    protected abstract int getNextLightLevelBlockId();
}
