package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.math.BlockFace;

public class SkullDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (item.getDamage() != ItemSkull.HEAD_WITHER_SKELETON) {
            return super.dispense(block, face, item);
        }

        Block target = block.getSide(face);

        //TODO: wither
        return null;
    }
}
