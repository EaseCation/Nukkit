package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public abstract class BlockNetherRoots extends BlockFlower {
    protected BlockNetherRoots() {
        super(0);
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return false;
    }

    @Override
    protected boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == COARSE_DIRT || id == FARMLAND || id == PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS
                || id == CRIMSON_NYLIUM || id == WARPED_NYLIUM || id == SOUL_SOIL;
    }
}
