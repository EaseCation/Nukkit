package cn.nukkit.block;

public class BlockBushFirefly extends BlockBush {
    public BlockBushFirefly() {
    }

    @Override
    public int getId() {
        return FIREFLY_BUSH;
    }

    @Override
    public String getName() {
        return "Firefly Bush";
    }

    @Override
    public boolean canBeReplaced() {
        return false;
    }

    @Override
    public int getLightLevel() {
        return 2;
    }

    @Override
    protected boolean canReplace(Block block) {
        Block extra;
        return block.canBeReplaced() && !block.isLiquid()
                && (extra = level.getExtraBlock(block)).canBeReplaced() && !extra.isWater();
    }

    @Override
    protected void spread(Block block) {
        if (block.is(SNOW_LAYER)) {
            level.setExtraBlock(block, get(AIR), true, false);
        }
        super.spread(block);
    }
}
