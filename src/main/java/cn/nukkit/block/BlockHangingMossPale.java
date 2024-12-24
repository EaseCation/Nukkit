package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockHangingMossPale extends BlockFlowable {
    public static final int TIP_BIT = 0b1;

    public BlockHangingMossPale() {
        this(0);
    }

    public BlockHangingMossPale(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_HANGING_MOSS;
    }

    @Override
    public String getName() {
        return "Pale Hanging Moss";
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
    public boolean canSilkTouch() {
        return true;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            Block head = this;
            Block below;
            while ((below = head.down()).getId() == getId()) {
                head = below;
            }

            if (!below.isAir()) {
                return true;
            }

            int minHeight = level.getHeightRange().getMinY();
            int headY = head.getFloorY();
            if (headY <= minHeight) {
                return true;
            }

            level.setBlock(head, get(getId()), true);
            level.setBlock(below, get(getId(), TIP_BIT), true);

            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
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
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected boolean canSurvive() {
        Block above = up();
        return above.getId() == getId() || SupportType.hasFullSupport(above, BlockFace.DOWN);
    }

    public boolean isTip() {
        return (getDamage() & TIP_BIT) != 0;
    }
}
