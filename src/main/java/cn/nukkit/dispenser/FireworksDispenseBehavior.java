package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class FireworksDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 dir = face.getUnitVector().asVector3();
        Vector3 pos = block.blockCenter().add(dir.multiply(0.5));
        if (item instanceof ItemFirework) {
            EntityFirework firework = ((ItemFirework) item).spawnFirework(block.getLevel(),pos,dir);
            firework.spawnToAll();
        }else {
            EntityFirework firework = new EntityFirework(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                    Entity.getDefaultNBT(pos, dir, (float) dir.yRotFromDirection(), (float) dir.xRotFromDirection()), true);
            firework.spawnToAll();
        }

        return null;

    }
}
