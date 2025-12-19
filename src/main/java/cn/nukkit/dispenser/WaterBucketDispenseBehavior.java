package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nullable;

public class WaterBucketDispenseBehavior extends DefaultDispenseBehavior {
    @Nullable
    private final EntityFactory entityFactory;

    public WaterBucketDispenseBehavior() {
        this(null);
    }

    public WaterBucketDispenseBehavior(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target.canBeFlowedInto() || target.canContainWater()) {
            if (!target.isAir() && target.canContainWater()) {
                target.level.setExtraBlock(target, Block.get(Block.FLOWING_WATER), true);
            } else {
                target.level.setExtraBlock(target, Block.get(Block.AIR), true, false);
                target.level.setBlock(target, Block.get(Block.FLOWING_WATER), true);
            }

            if (entityFactory != null) {
                entityFactory.create(target.getChunk(), Entity.getDefaultNBT(target.add(0.5, 0, 0.5), null, face.getHorizontalAngle(), 0)).spawnToAll();
            }

            return Item.get(ItemID.BUCKET);
        }
        return super.dispense(block, face, item);
    }
}
