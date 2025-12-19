package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class BoatDispenseBehavior extends DefaultDispenseBehavior {
    private final int boatType;
    private final EntityFactory factory;

    public BoatDispenseBehavior(int boatType, EntityFactory factory) {
        this.boatType = boatType;
        this.factory = factory;
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSideVec(face).multiply(1.125);

        Block target = block.getSide(face);

        if (target instanceof BlockWater) {
            pos.y += 1;
        } else if (target.getId() != BlockID.AIR || !(target.down() instanceof BlockWater)) {
            return super.dispense(block, face, item);
        }

        pos = target.getLocation().setYaw(face.getHorizontalAngle());

        Entity boat = factory.create(block.level.getChunk(target.getChunkX(), target.getChunkZ()),
                Entity.getDefaultNBT(pos, null, face.getHorizontalAngle() + 90, 0)
                        .putByte("woodID", boatType)
        );

        boat.spawnToAll();

        return null;
    }

}
