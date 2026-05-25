package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class FertilizerDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target.isFertilizable() && target.canBeActivated() && target.onActivate(item, face.getOpposite(), null)) {
            item.pop();
        }
        return null;
    }
}
