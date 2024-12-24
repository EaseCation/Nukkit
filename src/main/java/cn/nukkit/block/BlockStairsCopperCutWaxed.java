package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockStairsCopperCutWaxed extends BlockStairsCopperCut {
    public BlockStairsCopperCutWaxed() {
        this(0);
    }

    public BlockStairsCopperCutWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER_STAIRS;
    }

    @Override
    public String getName() {
        return "Waxed Cut Copper Stairs";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
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
    public final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_CUT_COPPER_STAIRS;
    }

    public int getDewaxedBlockId() {
        return CUT_COPPER_STAIRS;
    }
}
