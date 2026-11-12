package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * 合成全部库存动作成功后触发，供任务和统计使用；取消合成仍应监听 CraftItemEvent。
 */
public final class CraftItemCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final CraftItemEvent crafting;
    private final Item[] createdOutputs;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public CraftItemCompleteEvent(CraftItemEvent crafting) {
        this.crafting = crafting;
        Item[] inputs = crafting.getInputs();
        Item[] outputs = crafting.getOutputs();
        if (outputs.length == 0) {
            outputs = new Item[]{crafting.getResult()};
        }
        List<Item> created = new ArrayList<>(outputs.length);
        for (Item output : outputs) {
            for (Item input : inputs) {
                if (!input.isNull() && output.equals(input, true, true, true)) {
                    int retained = Math.min(output.getCount(), input.getCount());
                    output.setCount(output.getCount() - retained);
                    input.setCount(input.getCount() - retained);
                }
            }
            if (!output.isNull()) {
                created.add(output);
            }
        }
        createdOutputs = created.toArray(Item[]::new);
    }

    public Player getPlayer() {
        return crafting.getPlayer();
    }

    public Recipe getRecipe() {
        return crafting.getRecipe();
    }

    public int getRepetitions() {
        return crafting.getRepetitions();
    }

    public Item[] getInputs() {
        return crafting.getInputs();
    }

    public Item[] getOutputs() {
        return crafting.getOutputs();
    }

    public Item getResult() {
        return crafting.getResult();
    }

    /**
     * 返回净新增主产物，不把复制配方返回的原书或原旗帜计入产量。
     */
    public Item getCreatedResult() {
        Item primary = crafting.getResult();
        for (Item output : createdOutputs) {
            if (output.equals(primary, true, true, true)) {
                return output.clone();
            }
        }
        return Items.air();
    }

    public Item[] getCreatedOutputs() {
        Item[] outputs = new Item[createdOutputs.length];
        for (int index = 0; index < outputs.length; index++) {
            outputs[index] = createdOutputs[index].clone();
        }
        return outputs;
    }
}
