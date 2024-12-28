package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

public interface CopperBehavior {
    boolean isWaxed();

    int getCopperAge();

    int getWaxedBlockId();

    int getDewaxedBlockId();

    int getIncrementAgeBlockId();

    int getDecrementAgeBlockId();

    static boolean use(CopperBehavior behavior, Position target, Item item, Player player) {
        if (behavior.isWaxed()) {
            if (item.isAxe()) {
                if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                    item.pop();
                    player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                }

                target.level.addLevelEvent(target, LevelEventPacket.EVENT_PARTICLE_WAX_OFF);

                target.level.setBlock(target, Block.get(behavior.getDewaxedBlockId()), true);
                return true;
            }

            return false;
        }

        if (item.getId() == Item.HONEYCOMB) {
            if (player != null && !player.isCreative()) {
                item.pop();
            }

            target.level.addLevelEvent(target, LevelEventPacket.EVENT_PARTICLE_WAX_ON);

            target.level.setBlock(target, Block.get(behavior.getWaxedBlockId()), true);
            return true;
        }

        if (item.isAxe() && behavior.getCopperAge() > 0) {
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            target.level.addLevelEvent(target, LevelEventPacket.EVENT_PARTICLE_SCRAPE);

            target.level.setBlock(target, Block.get(behavior.getDecrementAgeBlockId()), true);
            return true;
        }

        return false;
    }

    static void randomTick(CopperBehavior behavior, Position target) {
        if (behavior.isWaxed()) {
            return;
        }

        int thisAge = behavior.getCopperAge();
        if (thisAge >= 3) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextFloat() >= 1638.4f * 0.000041666666f) {
            return;
        }

        int same = 0;
        int older = 0;

        BlockVector3 pos = target.asBlockVector3();
        for (int x = pos.x - 4; x <= pos.x + 4; x++) {
            for (int z = pos.z - 4; z <= pos.z + 4; z++) {
                for (int y = pos.y - 4; y <= pos.y + 4; y++) {
                    if (pos.distanceManhattan(x, y, z) > 4) {
                        continue;
                    }

                    Block block = target.level.getBlock(x, y, z);
                    if (!block.hasCopperBehavior() || block.isWaxed()) {
                        continue;
                    }

                    int age = block.getCopperAge();
                    if (age < thisAge) {
                        return;
                    }

                    if (age == thisAge) {
                        same++;
                    } else {
                        older++;
                    }
                }
            }
        }

        float ratio = (older + 1f) / (older + same + 1f);
        float chance = ratio * ratio;
        if (older == 0) {
            chance *= 0.75f;
        }
        if (random.nextFloat() >= chance) {
            return;
        }

        target.level.setBlock(target, Block.get(behavior.getIncrementAgeBlockId()), true);
    }
}