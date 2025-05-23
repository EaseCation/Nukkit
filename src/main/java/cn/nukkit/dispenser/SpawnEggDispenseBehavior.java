package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class SpawnEggDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSideVec(face).add(0.5, 0.5, 0.5);

        Entity entity = Entity.createEntity(item.getDamage(), block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos, null, face.getHorizontalAngle(), 0));

        boolean success = entity != null;

        if (success) {
            if (item.hasCustomName() && entity instanceof EntityLiving) {
                entity.setNameTag(item.getCustomName());
            }

            entity.spawnToAll();
            return null;
        }

        return super.dispense(block, face, item);
    }
}
