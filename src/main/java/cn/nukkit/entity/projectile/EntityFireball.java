package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public abstract class EntityFireball extends EntityProjectile {
    protected float xPower;
    protected float yPower;
    protected float zPower;

    public EntityFireball(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntityFireball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        ListTag<FloatTag> power = namedTag.getList("power", FloatTag.class);
        if (power.size() >= 3) {
            xPower = power.get(0).data;
            yPower = power.get(1).data;
            zPower = power.get(2).data;
        } else {
            xPower = 0;
            yPower = 0;
            zPower = 0;
        }

        dataProperties
                .putFloat(DATA_FIREBALL_POWER_X, xPower)
                .putFloat(DATA_FIREBALL_POWER_Y, yPower)
                .putFloat(DATA_FIREBALL_POWER_Z, zPower);

        if (shouldBurn()) {
            setOnFire(1);
            setDataFlag(DATA_FLAG_ONFIRE, true, false);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putList(new ListTag<>("power")
                .addFloat(xPower)
                .addFloat(yPower)
                .addFloat(zPower));
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (hasUpdate && shouldBurn()) {
            setOnFire(1);
        }

        if (age > 1200 || isCollided || !level.isValidHeight(y)) {
            close();
            hasUpdate = false;
        }

        return hasUpdate;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (reflectOnHurt() && source instanceof EntityDamageByEntityEvent) {
            switch (source.getCause()) {
                case ENTITY_ATTACK:
                case PROJECTILE:
                    Entity damager = ((EntityDamageByEntityEvent) source).getDamager();

                    Vector3 dir = Vector3.directionFromRotation(damager.pitch, damager.yaw);
                    motionX = dir.x;
                    motionY = dir.y;
                    motionZ = dir.z;

                    pitch = damager.pitch;
                    yaw = damager.yaw;

                    shootingEntity = damager;
            }
        }

        return super.attack(source);
    }

    @Override
    protected float getLiquidInertia() {
        return 1;
    }

    protected boolean reflectOnHurt() {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    public void setPower(float xPower, float yPower, float zPower) {
        this.xPower = xPower;
        this.yPower = yPower;
        this.zPower = zPower;

        setDataProperty(new FloatEntityData(DATA_FIREBALL_POWER_X, xPower));
        setDataProperty(new FloatEntityData(DATA_FIREBALL_POWER_Y, yPower));
        setDataProperty(new FloatEntityData(DATA_FIREBALL_POWER_Z, zPower));
    }

    public Vector3f getPower() {
        return new Vector3f(xPower, yPower, zPower);
    }
}
