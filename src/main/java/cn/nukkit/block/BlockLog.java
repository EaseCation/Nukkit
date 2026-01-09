package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.API;
import cn.nukkit.api.API.Definition;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.AnimatePacket.SwingSource;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public abstract class BlockLog extends BlockRotatedPillar {
    public static final int[] LOGS = {
            OAK_LOG,
            SPRUCE_LOG,
            BIRCH_LOG,
            JUNGLE_LOG,
    };
    public static final int[] LOGS2 = {
            ACACIA_LOG,
            DARK_OAK_LOG,
    };

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    @API(definition = Definition.INTERNAL)
    public static final int[] LOG2_NUKKIT_LEGACY_META_TO_NUKKIT_RUNTIME_META = {
            0b000, 0b001,  0b000, 0b001,
            0b010, 0b011,  0b010, 0b011,
            0b100, 0b101,  0b100, 0b101,
            0b000, 0b001,  0b000, 0b001 // 0b110, 0b111,  0b110, 0b111
    };

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
