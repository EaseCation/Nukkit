package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.AnimatePacket.SwingSource;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public abstract class BlockLog extends BlockRotatedPillar {
    protected BlockLog(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!item.isAxe()) {
            return false;
        }

        if (player != null) {
            player.swingArm(SwingSource.USE_ITEM);
            if (player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
        }

        level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

        level.setBlock(this, getStrippedBlock(), true);
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isLog() {
        return true;
    }

    protected abstract Block getStrippedBlock();
}
