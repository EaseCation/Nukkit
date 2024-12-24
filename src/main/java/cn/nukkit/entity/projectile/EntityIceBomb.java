package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class EntityIceBomb extends EntityProjectile {
    public static final int NETWORK_ID = EntityID.ICE_BOMB;

    public EntityIceBomb(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityIceBomb(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.025f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    protected float getLiquidInertia() {
        return 1;
    }

    @Override
    protected boolean dealImpactDamage() {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isClosed()) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (!isCollided) {
            for (Block block : getCollisionBlocks()) {
                if (block.isWater()) {
                    isCollided = true;
                    break;
                }
            }
        }

        if (isCollided || hadCollision) {
            int centerX = getFloorX();
            int centerY = getFloorY();
            int centerZ = getFloorZ();
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        int blockX = centerX + x;
                        int blockY = centerY + y;
                        int blockZ = centerZ + z;
                        if (level.getBlock(blockX, blockY, blockZ).isWater()) {
                            level.setBlock(blockX, blockY, blockZ, Block.get(Block.ICE), true);
                        }
                    }
                }
            }

            for (int i = 0; i < 6; i++) {
                level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_PROJECTILE_HIT, 15);
            }
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ICEBOMB_HIT);

            close();
            hasUpdate = true;
        } else if (age > 1200) {
            close();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
