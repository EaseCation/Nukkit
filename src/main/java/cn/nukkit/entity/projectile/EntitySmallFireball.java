package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

public class EntitySmallFireball extends EntityFireball {
    public static final int NETWORK_ID = EntityID.SMALL_FIREBALL;

    public EntitySmallFireball(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntitySmallFireball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.31f;
    }

    @Override
    public float getLength() {
        return 0.31f;
    }

    @Override
    public float getHeight() {
        return 0.31f;
    }

    @Override
    protected double getBaseDamage() {
        return 5;
    }

    @Override
    protected boolean shouldBurn() {
        return true;
    }

    @Override
    public boolean onCollideWithEntity(Entity entity) {
        if (entity.fireProof || entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            close();
            return true;
        }

        return super.onCollideWithEntity(entity);
    }

    @Override
    protected void onHitBlock(MovingObjectPosition blockHitResult) {
        Block block;
        if ((shootingEntity == null || shootingEntity instanceof EntityLiving && !(shootingEntity instanceof Player) || level.getGameRules().getBoolean(GameRule.MOB_GRIEFING))
                && (block = level.getBlock(blockHitResult.block.getSideVec(BlockFace.fromIndex(blockHitResult.sideHit)))).isAir()) {
            BlockFire.tryIgnite(block, null, this, BlockIgniteCause.FIREBALL);
        }

        super.onHitBlock(blockHitResult);
    }

    @Override
    public void spawnTo(Player player) {
        if (hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }
}
