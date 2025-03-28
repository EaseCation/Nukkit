package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Potion;

public class WaterBottleDispenseBehaviour extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (item.getDamage() != Potion.WATER) {
            return super.dispense(block, face, item);
        }

        Block target = block.getSide(face);
        int id = target.getId();
        if (id != Block.DIRT && id != Block.DIRT_WITH_ROOTS && id != Block.COARSE_DIRT) {
            return super.dispense(block, face, item);
        }

        if (target.level.setBlock(target, Block.get(Block.MUD), true)) {
            target.level.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_SPLASH, (int) (0.5f * 0xffffff));
        }
        return Item.get(Item.GLASS_BOTTLE);
    }
}
