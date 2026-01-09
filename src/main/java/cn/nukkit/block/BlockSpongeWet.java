package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Dimension;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;

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
            level.setBlock(block, Block.get(SPONGE), true, true);
            level.addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE);
            return true;
        }

        return level.setBlock(this, this, true, true);
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0;
    }
}
