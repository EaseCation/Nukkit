package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class EntityBucketDispenseBehavior extends DefaultDispenseBehavior {
    private final EntityFactory entityFactory;

    public EntityBucketDispenseBehavior(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 target = block.getSideVec(face);
        entityFactory.create(block.level.getChunk(target.getChunkX(), target.getChunkZ()), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0)).spawnToAll();
        return Item.get(ItemID.BUCKET);
    }
}
