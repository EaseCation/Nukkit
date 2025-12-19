package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;

public class PowderSnowBucketDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target.isAir()) {
            target.level.setBlock(target, Block.get(Block.POWDER_SNOW), true);
            return Item.get(ItemID.BUCKET);
        }
        return super.dispense(block, face, item);
    }
}
