package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;

/**
 * 砂轮祛魔/修理事件（可取消）
 * 插件可通过本事件调整返还的经验值，或直接取消本次操作
 */
public class GrindstoneEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    // 祛魔前的输入物品
    private final Item inputItem;
    // 祛魔后的输出物品
    private final Item outputItem;
    // 祛魔所用的材料（可能为空气）
    private final Item materialItem;
    // 操作玩家
    private final Player player;
    // 返还的经验值（可修改）
    private int experienceDropped;

    public GrindstoneEvent(GrindstoneInventory inventory,
                           Item inputItem,
                           Item outputItem,
                           Item materialItem,
                           int experienceDropped,
                           Player player) {
        super(inventory);
        this.inputItem = inputItem;
        this.outputItem = outputItem;
        this.materialItem = materialItem;
        this.experienceDropped = experienceDropped;
        this.player = player;
    }

    public Item getInputItem() {
        return inputItem;
    }

    public Item getOutputItem() {
        return outputItem;
    }

    public Item getMaterialItem() {
        return materialItem;
    }

    public Player getPlayer() {
        return player;
    }

    public int getExperienceDropped() {
        return experienceDropped;
    }

    public void setExperienceDropped(int experienceDropped) {
        this.experienceDropped = experienceDropped;
    }
}

