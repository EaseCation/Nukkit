package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.passive.EntityAbstractHorse;
import cn.nukkit.entity.passive.EntityCamel;
import cn.nukkit.entity.passive.EntityLlama;
import cn.nukkit.entity.passive.EntityTraderLlama;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.Items;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.UpdateEquipmentPacket;

import java.util.Objects;

public class HorseInventory extends ContainerInventory {
    private final Entity entity;

    public HorseInventory(Entity entity) {
        super((InventoryHolder) entity, InventoryType.HORSE);
        this.entity = entity;
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        super.onSlotChange(index, before, after, send);

        if (index == 0) {
            if (entity instanceof EntityAbstractHorse horse) {
                boolean saddle = after != null && after.is(Item.SADDLE);
                horse.updateSaddled(saddle);
                if (saddle && !after.equals(before)) {
                    entity.level.addLevelSoundEvent(entity, LevelSoundEventPacket.SOUND_SADDLE);
                }
            } else if (entity instanceof EntityCamel camel) {
                boolean saddle = after != null && after.is(Item.SADDLE);
                camel.updateSaddled(saddle);
                if (saddle && !after.equals(before)) {
                    entity.level.addLevelSoundEvent(entity, LevelSoundEventPacket.SOUND_SADDLE);
                }
            } else if ((entity instanceof EntityLlama || entity instanceof EntityTraderLlama) && !Objects.equals(before, after)) {
                if (after != null && after.is(ItemBlockID.CARPET)) {
                    entity.level.addLevelSoundEvent(entity, LevelSoundEventPacket.SOUND_ARMOR, entity.getIdentifier());
                }
                sendArmorContents(getViewers().toArray(new Player[0]));
            }
        } else if (index == 1 && entity instanceof EntityAbstractHorse && !Objects.equals(before, after)) {
            if (after != null && after.isHorseArmor()) {
                entity.level.addLevelSoundEvent(entity, LevelSoundEventPacket.SOUND_ARMOR, EntityFullNames.HORSE);
            }
            sendArmorContents(getViewers().toArray(new Player[0]));
        }
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);

        ListTag<CompoundTag> slots = new ListTag<>();
        switch (entity.getNetworkId()) {
            case EntityID.LLAMA:
            case EntityID.TRADER_LLAMA:
                slots.add(new CompoundTag()
                        .put("acceptedItems", new ListTag<CompoundTag>()
                                .add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                                        .putString("Name", "minecraft:carpet")
                                        .putShort("Aux", Short.MAX_VALUE)))
                        )
                        .putCompound("item", new CompoundTag()
                                .putString("Name", "minecraft:carpet")
                                .putShort("Aux", Short.MAX_VALUE))
                        .putInt("slotNumber", 1));
                break;
            case EntityID.HORSE:
                slots.add(new CompoundTag()
                        .put("acceptedItems", new ListTag<CompoundTag>()
                                .add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                                        .putString("Name", "minecraft:horsearmorleather")
                                        .putShort("Aux", Short.MAX_VALUE)))
                                .add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                                        .putString("Name", "minecraft:horsearmoriron")
                                        .putShort("Aux", Short.MAX_VALUE)))
                                .add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                                        .putString("Name", "minecraft:horsearmorgold")
                                        .putShort("Aux", Short.MAX_VALUE)))
                                .add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                                        .putString("Name", "minecraft:horsearmordiamond")
                                        .putShort("Aux", Short.MAX_VALUE)))
                        )
                        .putCompound("item", new CompoundTag()
                                .putString("Name", "minecraft:horsearmoriron")
                                .putShort("Aux", Short.MAX_VALUE))
                        .putInt("slotNumber", 1));
            default:
                slots.add(new CompoundTag()
                        .put("acceptedItems", new ListTag<CompoundTag>()
                                .add(new CompoundTag().putCompound("slotItem", new CompoundTag()
                                        .putString("Name", "minecraft:saddle")
                                        .putShort("Aux", Short.MAX_VALUE)))
                        )
                        .putCompound("item", new CompoundTag()
                                .putString("Name", "minecraft:saddle")
                                .putShort("Aux", Short.MAX_VALUE))
                        .putInt("slotNumber", 0));
        }

        UpdateEquipmentPacket pk = new UpdateEquipmentPacket();
        pk.windowId = who.getWindowId(this);
        pk.windowType = getType().getNetworkType();
        pk.size = 0;
        pk.eid = entity.getId();
        pk.namedtag = new CompoundTag().put("slots", slots);
        who.dataPacket(pk);

        super.sendContents(who);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        super.sendSlot(index, players);

        int entityType = entity.getNetworkId();
        if (index == 1 && entityType == EntityID.HORSE
                || index == 0 && (entityType == EntityID.LLAMA || entityType == EntityID.TRADER_LLAMA)) {
            sendArmorContents(players);
        }
    }

    @Override
    public void sendContents(Player... players) {
        super.sendContents(players);

        sendArmorContents(players);
    }

    public void sendArmorContents(Player... players) {
        MobArmorEquipmentPacket pk;
        int entityType = entity.getNetworkId();
        if (entityType == EntityID.HORSE) {
            Item body = getItem(1);
            Item empty = Items.air();
            Item[] slots = new Item[4];
            slots[0] = empty;
            slots[1] = body;
            slots[2] = empty;
            slots[3] = empty;
            pk = new MobArmorEquipmentPacket();
            pk.eid = entity.getId();
            pk.slots = slots;
            pk.body = body;
        } else if (entityType == EntityID.LLAMA || entityType == EntityID.TRADER_LLAMA) {
            Item body = getItem(0);
            Item empty = Items.air();
            Item[] slots = new Item[4];
            slots[0] = empty;
            slots[1] = body;
            slots[2] = empty;
            slots[3] = empty;
            pk = new MobArmorEquipmentPacket();
            pk.eid = entity.getId();
            pk.slots = slots;
            pk.body = body;
        } else {
            return;
        }
        for (Player player : players) {
            player.dataPacket(pk);
        }
    }
}
