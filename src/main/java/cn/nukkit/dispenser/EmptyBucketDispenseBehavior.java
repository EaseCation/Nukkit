package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

/**
 * @author CreeperFace
 */
public class EmptyBucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSideVec(face);
        Block target = block.level.getBlock(pos);

        if (target.isLiquidSource()) {
            target.level.setBlock(target, Block.get(BlockID.AIR), true);
            return new ItemBucket(ItemBucket.getMetaFromBlock(target.getId()));
        } else {
            target = block.level.getExtraBlock(pos);
            if (target.isLiquidSource()) {
                target.level.setExtraBlock(target, Block.get(BlockID.AIR), true);
                return new ItemBucket(ItemBucket.getMetaFromBlock(target.getId()));
            }
        }

        super.dispense(block, face, item);
        return null;
    }
}
