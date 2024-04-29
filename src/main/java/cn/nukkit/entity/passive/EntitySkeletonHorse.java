package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntitySkeletonHorse extends EntityAbstractHorse implements EntitySmite {

    public static final int NETWORK_ID = EntityID.SKELETON_HORSE;

    public EntitySkeletonHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Skeleton Horse";
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        dataProperties.putByte(DATA_HORSE_TYPE, HORSE_TYPE_SKELETON);

        setDataFlag(DATA_FLAG_TAMED, true, false);
        setDataFlag(DATA_FLAG_WASD_CONTROLLED, true, false);
        setDataFlag(DATA_FLAG_CAN_POWER_JUMP, true, false);

        movementSpeed = 0.2f;

        this.setMaxHealth(15);
    }

    @Override
    public Item[] getDrops() {
        // Skeleton horse doesn't drop inventory
        return new Item[] {
                Item.get(Item.BONE, 0, ThreadLocalRandom.current().nextInt(3)),
        };
    }

    @Override
    public void openInventory(Player player) {
        // Skeleton horse doesn't have inventory. So do not open it
    }

    @Override
    public boolean canRide() {
        // Skeleton horse can ride without saddle
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

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        if (entity.getNetworkId() == -1) {
            return new Vector3f(0, 2.42001f, -0.2f);
        }
        return new Vector3f(0, 1.2f + entity.getRidingOffset(), -0.2f);
    }

    @Override
    public boolean canDoInteraction(Player player) {
        if (isBaby()) {
            return false;
        }
        return passengers.isEmpty();
    }

    @Override
    public void updateSaddled(boolean saddled, boolean send) {
    }

    @Override
    public float getJumpStrength() {
        return 0.7f; //TODO: 0.4-1.0
    }

    @Override
    protected boolean isClientPredictedMovement() {
        return false;
    }

    @Override
    public void onPlayerInput(Player player, double motionX, double motionY) {
        if (!player.isServerAuthoritativeMovementEnabled()) {
            return;
        }

        motionX *= 0.4f;
        if (motionY > 0) {
            motionY *= 0.7f;
        } else if (motionY < 0) {
            motionY *= 0.2f;
        }

        double f = motionX * motionX + motionY * motionY;
        double friction = 0.6;

        this.pitch = Mth.clamp(player.pitch, -44.949997f, 44.949997f);
        this.yaw = player.yaw;

        if (f >= 1.0E-4) {
            f = Math.sqrt(f);

            if (f < 1) {
                f = 1;
            }

            f = friction / f;
            motionX = motionX * f;
            motionY = motionY * f;
            double d = this.yaw * Mth.DEG_TO_RAD;
            double f1 = Mth.sin(d);
            double f2 = Mth.cos(d);
            this.motionX = (motionX * f2 - motionY * f1);
            this.motionZ = (motionY * f2 + motionX * f1);
        } else {
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    @Override
    public void onPlayerInput(Player player, double x, double y, double z, double yaw, double pitch) {
        if (player.isServerAuthoritativeMovementEnabled()) {
            return;
        }
        super.onPlayerInput(player, x, y, z, yaw, pitch);
    }
}
