package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class TNTDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSideVec(face).add(0.5, 0, 0.5);

        EntityPrimedTNT tnt = new EntityPrimedTNT(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos).putBoolean("AllowUnderwater", (item.getDamage() & BlockTNT.ALLOW_UNDERWATER_BIT) != 0));
        tnt.spawnToAll();

        return null;
    }

}
