package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleDamageEvent;
import cn.nukkit.event.vehicle.VehicleDestroyEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityVehicle extends Entity implements EntityRideable, EntityInteractable {
    public static final Vector3f MOB_SEAT_OFFSET = new Vector3f(0, -0.2f, 0);
    public static final Vector3f PLAYER_SEAT_OFFSET = new Vector3f(0, 1.02001f, 0);

    protected boolean rollingDirection = true;

    public EntityVehicle(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.dataProperties.putInt(DATA_HURT_TIME, 0);
        this.dataProperties.putInt(DATA_HURT_DIRECTION, 1);
    }

    public int getRollingAmplitude() {
        return this.getDataPropertyInt(DATA_HURT_TIME);
    }

    public void setRollingAmplitude(int time) {
        this.setDataProperty(new IntEntityData(DATA_HURT_TIME, time));
    }

    public int getRollingDirection() {
        return this.getDataPropertyInt(DATA_HURT_DIRECTION);
    }

    public void setRollingDirection(int direction) {
        this.setDataProperty(new IntEntityData(DATA_HURT_DIRECTION, direction));
    }

    public int getDamage() {
        return this.getDataPropertyInt(DATA_HEALTH); // false data name (should be DATA_DAMAGE_TAKEN)
    }

    public void setDamage(int damage) {
        this.setDataProperty(new IntEntityData(DATA_HEALTH, damage));
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.mount";
    }

    @Override
    public boolean canDoInteraction(Player player) {
        return passengers.isEmpty();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }

        // A killer task
        if (y < level.getHeightRange().getMinY() - 18) {
            close();
            return false;
        }

        // Movement code
        updateMovement();
        return true;
    }

    protected boolean performHurtAnimation() {
        setRollingAmplitude(9);
        setRollingDirection(rollingDirection ? 1 : -1);
        rollingDirection = !rollingDirection;
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        VehicleDamageEvent event = new VehicleDamageEvent(this, source.getEntity(), source.getFinalDamage());
        getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        boolean instantKill = false;

        if (source instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            instantKill = damager instanceof Player && ((Player) damager).isCreative();
        }

        if (instantKill || getHealth() - source.getFinalDamage() < 1) {
            VehicleDestroyEvent event2 = new VehicleDestroyEvent(this, source.getEntity());
            getServer().getPluginManager().callEvent(event2);

            if (event2.isCancelled()) {
                return false;
            }
        }

        if (instantKill) {
            source.setDamage(1000);
        }

        return super.attack(source);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        if (entity.getNetworkId() == -1) {
            return PLAYER_SEAT_OFFSET;
        }
        return MOB_SEAT_OFFSET.add(0, entity.getRidingOffset(), 0);
    }
}
