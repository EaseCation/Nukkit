package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockNetherSprouts extends BlockFlowable {
    public BlockNetherSprouts() {
        super(0);
    }

    @Override
    public int getId() {
        return BLOCK_NETHER_SPROUTS;
    }

    @Override
    public String getName() {
        return "Nether Sprouts";
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHEARS;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
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
    public Item[] getDrops(Item item, Player player) {
        if (!item.isShears()) {
            return new Item[0];
        }

        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public boolean canContainSnow() {
        return true;
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
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == PODZOL || id == MYCELIUM || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS
                || id == CRIMSON_NYLIUM || id == WARPED_NYLIUM || id == SOUL_SOIL;
    }
}
