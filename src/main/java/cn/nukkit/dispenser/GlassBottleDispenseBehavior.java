package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class GlassBottleDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.isWaterSource()) {
            return Item.get(Item.POTION);
        }

        if (target instanceof BlockBeehive beehive && beehive.getHoneyLevel() == BlockBeehive.MAX_HONEY_LEVEL) {
            beehive.setHoneyLevel(0);
            beehive.level.setBlock(beehive, beehive, true);

            return Item.get(Item.HONEY_BOTTLE);
        }

        return super.dispense(block, face, item);
    }
}
