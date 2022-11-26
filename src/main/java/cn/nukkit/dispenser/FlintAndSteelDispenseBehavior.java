package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class FlintAndSteelDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.isAir()) {
            BlockFire.tryIgnite(target, block, null, BlockIgniteCause.FLINT_AND_STEEL);
        } else if (target.isCampfire()) {
            ((BlockCampfire) target).tryLightFire(block, null, BlockIgniteCause.FLINT_AND_STEEL);
        } else if (target.getId() == BlockID.TNT) {
            target.onActivate(item, face, null);
        } else {
            item.count++;
            return null;
        }

        item.setDamage(item.getDamage() + 1);
        item.count++;
        return null;
    }
}
