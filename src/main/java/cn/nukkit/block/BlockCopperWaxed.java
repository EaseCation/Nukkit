package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;

public class BlockCopperWaxed extends BlockCopper {
    public BlockCopperWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Block of Copper";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isAxe()) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }

            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_WAX_OFF);

            level.setBlock(this, get(getDewaxedBlockId()), true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

    @Override
    protected final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER;
    }

    protected int getDewaxedBlockId() {
        return COPPER_BLOCK;
    }
}
