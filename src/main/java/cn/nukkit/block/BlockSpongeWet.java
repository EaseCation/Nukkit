package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockSpongeWet extends BlockSponge {
    BlockSpongeWet() {

    }

    @Override
    public int getId() {
        return WET_SPONGE;
    }

    @Override
    public String getName() {
        return "Wet Sponge";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (level.getDimension() == Dimension.NETHER) {
            evaporateWater(false);
            return true;
        }

        if (!level.setBlock(this, this, true)) {
            return false;
        }

        if (isWarmBiome()) {
            setShouldDry(this);
        }
        return true;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0;
    }

    @Override
    public String getDescriptionId() {
        return "tile.sponge.wet.name";
    }

    @Override
    public int onUpdate(int type) {
/*
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isWarmBiome()) {
                setShouldDry(this);
            }
            return type;
        }
*/
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            evaporateWater(false);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }
}
