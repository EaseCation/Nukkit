package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.CopperBehavior;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;

public class HoneycombDispenseBehaviour extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.hasCopperBehavior() && target instanceof CopperBehavior copper && !copper.isWaxed()) {
            if (target.level.setBlock(target, Block.get(copper.getWaxedBlockId(), copper.getDamage()), true)) {
                target.level.addLevelEvent(target.blockCenter(), LevelEventPacket.EVENT_PARTICLE_WAX_ON);
            }
            return null;
        }

        return super.dispense(block, face, item);
    }
}
