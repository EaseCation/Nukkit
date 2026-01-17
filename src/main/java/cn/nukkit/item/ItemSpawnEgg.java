package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entities;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSpawnEgg extends Item {

    public ItemSpawnEgg() {
        this(0, 1);
    }

    public ItemSpawnEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEgg(Integer meta, int count) {
        super(SPAWN_EGG, meta, count, "Spawn Egg");
    }

    protected ItemSpawnEgg(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public String getDescriptionId() {
        String entityId = Entities.getIdentifierByType(getEntityId(), /*!isVanilla()*/false);
        if (entityId == null) {
            entityId = "unknown";
        }
        return "item.spawn_egg.entity." + entityId + ".name";
    }

    @Override
    public boolean isStackedByData() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (!player.canPlaceOn(target, this)) {
            return false;
        }

        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        double y = Double.MIN_VALUE;
        AxisAlignedBB[] aabbs = target.getCollisionShape();
        if (aabbs == null || aabbs.length == 0) {
            y = block.getY();
        } else {
            for (AxisAlignedBB aabb : aabbs) {
                y = Math.max(y, aabb.getMaxY());
            }
        }
        y += 0.0001f;

        CompoundTag nbt = Entity.getDefaultNBT(block.getX() + 0.5, y, block.getZ() + 0.5, null, ThreadLocalRandom.current().nextFloat() * 360, 0);

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        CreatureSpawnEvent ev = new CreatureSpawnEvent(this.getEntityId(), block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        Entity entity = Entity.createEntity(this.getEntityId(), chunk, nbt);

        if (entity != null) {
            if (player.isSurvival()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            entity.spawnToAll();
            return true;
        }

        return false;
    }

    @Override
    public boolean isSpawnEgg() {
        return true;
    }

    public int getEntityId() {
        return getDamage();
    }
}
