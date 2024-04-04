package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockDoubleSlabCopperCutWaxed extends BlockDoubleSlabCopperCut {
    public BlockDoubleSlabCopperCutWaxed() {
        this(0);
    }

    public BlockDoubleSlabCopperCutWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Waxed Cut Copper Slab";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isAxe()) {
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_WAX_OFF);

            level.setBlock(this, get(getDewaxedBlockId(), getDamage()), true);
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
    protected int getSlabBlockId() {
        return WAXED_CUT_COPPER_SLAB;
    }

    @Override
    protected final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    protected int getDewaxedBlockId() {
        return DOUBLE_CUT_COPPER_SLAB;
    }
}
