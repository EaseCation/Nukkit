package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.NBTEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntityOminousItemSpawner extends Entity {
    public static final int NETWORK_ID = EntityID.OMINOUS_ITEM_SPAWNER;

    private Item item;
    private int ticksBeforeRemoval;

    public EntityOminousItemSpawner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("item")) {
            item = NBTIO.getItemHelper(namedTag.getCompound("item"));
        }
        dataProperties.putNBT(DATA_DISPLAY_ITEM, NBTIO.putItemHelper(item));

        if (namedTag.contains("ticks_before_removal")) {
            ticksBeforeRemoval = namedTag.getInt("ticks_before_removal");
        } else  {
            ticksBeforeRemoval = ThreadLocalRandom.current().nextInt(3 * 20, 6 * 20 + 1);
        }
        dataProperties.putInt(DATA_TICKS_BEFORE_REMOVAL, ticksBeforeRemoval);

        dataProperties.putLong(DATA_FLAGS, 0);
        fireProof = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putCompound("item", NBTIO.putItemHelper(item));

        namedTag.putInt("ticks_before_removal", ticksBeforeRemoval);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isClosed()) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }
        lastUpdate = currentTick;

        ticksBeforeRemoval -= tickDiff;
        if (ticksBeforeRemoval > 0) {
            return true;
        }

        //TODO: spawn item

        close();
        return false;
    }

    @Override
    public void spawnTo(Player player) {
        if (getViewers().containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        setDataProperty(new NBTEntityData(DATA_DISPLAY_ITEM, NBTIO.putItemHelper(item)));
    }

    public int getTicksBeforeRemoval() {
        return ticksBeforeRemoval;
    }

    public void setTicksBeforeRemoval(int ticks) {
        ticksBeforeRemoval = ticks;
        setDataProperty(new IntEntityData(DATA_TICKS_BEFORE_REMOVAL, ticks));
    }
}
