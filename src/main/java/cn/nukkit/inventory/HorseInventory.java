package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityAbstractHorse;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.UpdateEquipmentPacket;

public class HorseInventory extends ContainerInventory {

    public HorseInventory(EntityAbstractHorse horse) {
        super(horse, InventoryType.HORSE);
    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        super.onSlotChange(index, before, after, send);
        if (index == 0) getHolder().updateSaddled(getItem(0).getId() == Item.SADDLE);
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);

        UpdateEquipmentPacket pk = new UpdateEquipmentPacket();
        pk.windowId = who.getWindowId(this);
        pk.windowType = getType().getNetworkType();
        pk.size = getSize();
        pk.eid = getHolder().getId();
        pk.namedtag = new CompoundTag().put("slots", new ListTag<CompoundTag>()
            .add(new CompoundTag()
                .put("acceptedItems", new ListTag<CompoundTag>().add(new CompoundTag()
                    .putCompound("slotItem", new CompoundTag()
                        .putShort("Aux", Short.MAX_VALUE)
                        .putString("Name", "minecraft:saddle")
                    )
                ))
                .putCompound("item", new CompoundTag()
                    .putShort("Aux", Short.MAX_VALUE)
                    .putString("Name", "minecraft:saddle")
                )
                .putInt("slotNumber", 0)
            )
            .add(new CompoundTag()
                .put("acceptedItems", new ListTag<CompoundTag>()
                    .add(new CompoundTag()
                        .putCompound("slotItem", new CompoundTag()
                            .putShort("Aux", Short.MAX_VALUE)
                            .putString("Name", "minecraft:horsearmorleather")
                        )
                    )
                    .add(new CompoundTag()
                        .putCompound("slotItem", new CompoundTag()
                            .putShort("Aux", Short.MAX_VALUE)
                            .putString("Name", "minecraft:horsearmoriron")
                        )
                    )
                    .add(new CompoundTag()
                        .putCompound("slotItem", new CompoundTag()
                            .putShort("Aux", Short.MAX_VALUE)
                            .putString("Name", "minecraft:horsearmorgold")
                        )
                    )
                    .add(new CompoundTag()
                        .putCompound("slotItem", new CompoundTag()
                            .putShort("Aux", Short.MAX_VALUE)
                            .putString("Name", "minecraft:horsearmordiamond")
                        )
                    )
                )
                .putCompound("item", new CompoundTag()
                    .putShort("Aux", Short.MAX_VALUE)
                    .putString("Name", "minecraft:horsearmoriron")
                )
                .putInt("slotNumber", 1)
            )
        );

        who.dataPacket(pk);
        this.sendContents(who);
    }

    @Override
    public EntityAbstractHorse getHolder() {
        return (EntityAbstractHorse) this.holder;
    }
}
