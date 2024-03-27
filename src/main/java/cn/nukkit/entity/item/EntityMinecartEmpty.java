package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MinecartType;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartEmpty extends EntityMinecartAbstract {

    public static final int NETWORK_ID = EntityID.MINECART;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityMinecartEmpty(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(0);
    }

    @Override
    public boolean isRideable() {
        return true;
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {
        if (flag && this.getHealth() > 15
                && this.attack(new EntityDamageByBlockEvent(this.level.getBlock(x, y, z), this, DamageCause.CONTACT, 1))
                && !this.passengers.isEmpty()) {
            this.dismountEntity(this.getPassenger());
        }
    }

    @Override
    public String getInteractButtonText(Player player) {
        return this.passengers.isEmpty() ? "action.interact.ride.minecart" : "";
    }

    @Override
    public void onPlayerInput(Player player, double motionX, double motionY) {
        setCurrentSpeed(motionY);
    }

    @Override
    protected void prepareDataProperty() {
        super.prepareDataProperty();
        dataProperties.putByte(DATA_CONTROLLING_SEAT_INDEX, 0);
    }
}
