package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Leonidius20 on 20.08.18.
 */
public class FoodChorusFruit extends FoodNormal {

    public FoodChorusFruit() {
        super(4, 2.4F);
        addRelative(Item.CHORUS_FRUIT);
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);

        Level level = player.getLevel();
        HeightRange heightRange = level.getHeightRange();
        int minHeight = heightRange.getMinY();

        // Teleportation
        int minX = player.getFloorX() - 8;
        int minY = Math.max(player.getFloorY() - 8, minHeight);
        int minZ = player.getFloorZ() - 8;
        int maxX = minX + 16;
        int maxY = Math.min(minY + 16, heightRange.getMaxY());
        int maxZ = minZ + 16;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        XZ:
        for (int attempts = 0; attempts < 16; attempts++) {
            int x = random.nextInt(minX, maxX);
            int y = random.nextInt(minY, maxY);
            int z = random.nextInt(minZ, maxZ);

            while (!level.getBlock(x, y + 1, z).isSolid()) {
                y--;
                if (y < minHeight - 1) {
                    continue XZ;
                }
            }
            y++; // Back up to non solid

            Block blockUp = level.getBlock(x, y + 1, z);
            Block blockUp2 = level.getBlock(x, y + 2, z);

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid ||
                    blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            // Sounds are broadcast at both source and destination
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);
            player.teleport(new Vector3(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);

            break;
        }

        return true;
    }

}