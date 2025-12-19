package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        int resultItemId = -1;
        if (target.is(Block.POWDER_SNOW)) {
            resultItemId = Item.POWDER_SNOW_BUCKET;
        } else if (target.isWaterSource()) {
            resultItemId = Item.WATER_BUCKET;
        } else if (target.isLava() && target.isLiquidSource()) {
            resultItemId = Item.LAVA_BUCKET;
        }
        if (resultItemId != -1) {
            target.level.setBlock(target, Block.get(Block.AIR), true);
            return Item.get(resultItemId);
        }

        if (!target.isAir() && target.canContainWater()) {
            Block extra = target.level.getExtraBlock(target);
            if (extra.isWaterSource()) {
                target.level.setExtraBlock(target, Block.get(Block.AIR), true);
                return Item.get(Item.WATER_BUCKET);
            }
        }

        return super.dispense(block, face, item);
    }
}
