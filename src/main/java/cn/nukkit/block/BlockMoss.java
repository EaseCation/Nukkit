package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockMoss extends BlockSolid {
    public BlockMoss() {
    }

    @Override
    public int getId() {
        return MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Moss Block";
    }

    @Override
    public float getHardness() {
        return 0.1f;
    }

    @Override
    public float getResistance() {
        return 0.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isBoneMeal()) {
            if (!up().isAir()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(upVec()));

            //TODO
            return true;
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
