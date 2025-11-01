package cn.nukkit.entity.weather;

import cn.nukkit.Difficulty;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.BlockFire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by boybook on 2016/2/27.
 */
public class EntityLightning extends Entity implements EntityLightningStrike {

    public static final int NETWORK_ID = EntityID.LIGHTNING_BOLT;

    protected boolean isEffect = true;

    public int state;
    public int liveTime;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityLightning(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealth(4);
        this.setMaxHealth(4);

        this.state = 2;
        Random random = ThreadLocalRandom.current();
        this.liveTime = random.nextInt(3) + 1;

        tryIgniteBlock();
    }

    public boolean isEffect() {
        return this.isEffect;
    }

    public void setEffect(boolean e) {
        this.isEffect = e;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        this.entityBaseTick(tickDiff);

        if (this.state == 2) {
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_THUNDER);
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_EXPLODE);
        }

        this.state--;

        if (this.state < 0) {
            if (this.liveTime == 0) {
                this.close();
                return false;
            } else if (this.state < -ThreadLocalRandom.current().nextInt(10)) {
                this.liveTime--;
                this.state = 1;

                tryIgniteBlock();
            }
        }

        if (this.state >= 0) {
            if (this.isEffect) {
                AxisAlignedBB bb = getBoundingBox().grow(3, 3, 3);

                for (Entity entity : this.level.getCollidingEntities(bb, this)) {
                    entity.onStruckByLightning(this);
                }
            }
        }

        return true;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    private void tryIgniteBlock() {
        if (!isEffect || !level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) || level.getDifficulty() < Difficulty.NORMAL.ordinal()) {
            return;
        }
        Block block = getLevelBlock();

        if (block.isCampfire()) {
            ((BlockCampfire) block).tryLightFire(null, this, BlockIgniteCause.LIGHTNING);
            return;
        }

        if (!block.isAir()) {
            return;
        }
        BlockFire.tryIgnite(block, null, this, BlockIgniteCause.LIGHTNING);
    }
}
