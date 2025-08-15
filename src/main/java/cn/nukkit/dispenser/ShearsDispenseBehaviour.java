package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class ShearsDispenseBehaviour extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.isBeehive() && target.onActivate(item, face, null)) {
            if (item.hurtAndBreak(1) >= 0) {
                item.count++;
            }
            return null;
        }

        item.count++;
        return null;
    }
}
