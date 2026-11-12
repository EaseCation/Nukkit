package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftItemEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    private final CraftingTransaction transaction;
    private final Player player;
    private final Recipe recipe;
    private final int repetitions;
    private final Item[] inputs;
    private final Item[] outputs;

    public CraftItemEvent(CraftingTransaction transaction, Recipe recipe, int repetitions, Item[] inputs, Item[] outputs) {
        this.transaction = transaction;
        this.player = transaction.getSource();
        this.recipe = recipe;
        this.repetitions = repetitions;
        this.inputs = copyItems(inputs);
        this.outputs = aggregateOutputs(recipe, outputs);
    }

    public CraftItemEvent(CraftingTransaction transaction) {
        this(transaction, transaction.getRecipe(), transaction.getRepetitions(),
                transaction.getInputList().toArray(Item[]::new), new Item[0]);
    }

    public CraftItemEvent(Player player, Item[] input, Recipe recipe) {
        this.transaction = null;
        this.player = player;
        this.recipe = recipe;
        this.repetitions = 1;
        this.inputs = copyItems(input);
        this.outputs = new Item[0];
    }

    public Item[] getInput() {
        return getInputs();
    }

    @Nullable
    public CraftingTransaction getTransaction() {
        return transaction;
    }

    public Item[] getInputs() {
        return copyItems(inputs);
    }

    public Item[] getOutputs() {
        return copyItems(outputs);
    }

    /**
     * 返回本次合成的实际主产物，合并分散到多个库存槽的同种产物。
     * 该数量包含配方返回的原物品；成功后的新增产物统计应使用完成事件。
     */
    public Item getResult() {
        if (outputs.length != 0) {
            return outputs[0].clone();
        }
        Item legacyResult = transaction == null ? null : transaction.getPrimaryOutput();
        return legacyResult == null ? recipe.getResult() : legacyResult.clone();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public Player getPlayer() {
        return player;
    }

    private static Item[] copyItems(Item[] items) {
        Item[] copy = new Item[items.length];
        for (int index = 0; index < items.length; index++) {
            copy[index] = items[index].clone();
        }
        return copy;
    }

    private static Item[] aggregateOutputs(Recipe recipe, Item[] outputs) {
        List<Item> aggregated = new ArrayList<>(outputs.length);
        for (Item output : outputs) {
            if (output.isNull()) {
                continue;
            }
            Item existing = null;
            for (Item candidate : aggregated) {
                if (candidate.equals(output, true, true, true)) {
                    existing = candidate;
                    break;
                }
            }
            if (existing == null) {
                aggregated.add(output.clone());
            } else {
                existing.setCount(Math.addExact(existing.getCount(), output.getCount()));
            }
        }
        if (aggregated.size() > 1 && !(recipe instanceof MultiRecipe)) {
            Item template = recipe.getResult();
            int primary = -1;
            if (template != null) {
                for (int index = 0; index < aggregated.size(); index++) {
                    Item candidate = aggregated.get(index);
                    if (candidate.getId() == template.getId() && primary < 0) {
                        primary = index;
                    }
                    if (candidate.equals(template, true, true, true)) {
                        primary = index;
                        break;
                    }
                }
            }
            if (primary > 0) {
                aggregated.addFirst(aggregated.remove(primary));
            }
        }
        return aggregated.toArray(Item[]::new);
    }
}
