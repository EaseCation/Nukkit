package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.utils.BlockColor;

public class BlockDryGrassShort extends BlockBush {
    public BlockDryGrassShort() {
    }

    @Override
    public int getId() {
        return SHORT_DRY_GRASS;
    }

    @Override
    public String getName() {
        return "Short Dry Grass";
    }

    @Override
    public int getFuelTime() {
        return 100;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected boolean canSurvive(Block below) {
        int id = below.getId();
        return super.canSurvive(below) || id == Block.SAND || id == RED_SAND || id == SUSPICIOUS_SAND || below.isTerracotta();
    }

    @Override
    protected boolean onFertilize(Item item, Player player) {
        level.setBlock(this, get(TALL_DRY_GRASS), true);

        if (player != null && !player.isCreative()) {
            item.pop();
        }

        level.addParticle(new BoneMealParticle(this));
        return true;
    }

    protected boolean onFertilizeSpread(Item item, Player player) {
        return super.onFertilize(item, player);
    }
}
