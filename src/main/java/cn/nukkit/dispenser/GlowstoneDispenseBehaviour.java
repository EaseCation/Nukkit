package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockRespawnAnchor;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class GlowstoneDispenseBehaviour extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.is(Block.RESPAWN_ANCHOR) && target.getDamage() < BlockRespawnAnchor.MAX_CHARGE) {
            target.setDamage(target.getDamage() + 1);
            if (target.level.setBlock(target, target, true)) {
                target.level.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_RESPAWN_ANCHOR_CHARGE);
            }
            return null;
        }

        return super.dispense(block, face, item);
    }
}
