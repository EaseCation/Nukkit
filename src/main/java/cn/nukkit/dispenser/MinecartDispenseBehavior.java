package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class MinecartDispenseBehavior extends DefaultDispenseBehavior {

    private final EntityFactory factory;

    public MinecartDispenseBehavior(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target instanceof BlockRail) {
            target.x += 0.5;
            target.y += 0.125;
            target.z += 0.5;
        } else {
            return super.dispense(block, face, item);
        }

        Entity minecart = factory.create(target.getChunk(), Entity.getDefaultNBT(target));
        minecart.spawnToAll();

        return null;
    }
}
