package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockDirtRooted extends BlockSolid {
    public BlockDirtRooted() {
    }

    @Override
    public int getId() {
        return DIRT_WITH_ROOTS;
    }

    @Override
    public String getName() {
        return "Rooted Dirt";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isBoneMeal()) {
            if (!down().isAir()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            level.setBlock(downVec(), get(HANGING_ROOTS), true);
            return true;
        }

        if (item.isHoe()) {
            if (!up().isAir()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }

            level.setBlock(this, get(DIRT), true);

            level.dropItem(upVec(), Item.get(getItemId(HANGING_ROOTS)));
            return true;
        }

        if (item.isShovel()) {
            if (!up().isAir()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }

            level.setBlock(this, get(GRASS_PATH), true);
            return true;
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
