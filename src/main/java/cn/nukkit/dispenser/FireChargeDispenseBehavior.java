package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.concurrent.ThreadLocalRandom;

public class FireChargeDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 dir = face.getUnitVector().asVector3();
        Vector3 pos = block.blockCenter().add(dir.multiply(0.9));

        block.level.addLevelEvent(pos, LevelEventPacket.EVENT_SOUND_GHAST_SHOOT, 78642);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        dir = dir.add(10 * 0.0075 * random.nextGaussian(), 10 * 0.0075 * random.nextGaussian(), 10 * 0.0075 * random.nextGaussian());

        EntitySmallFireball fireball = new EntitySmallFireball(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos, dir.multiply(1.3), (float) dir.yRotFromDirection(), (float) dir.xRotFromDirection()));
        fireball.spawnToAll();

        return null;
    }
}
