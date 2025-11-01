package cn.nukkit.loot;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.RandomSource;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record LootTable(
        LootPool[] pools
) {
    public static final LootTable EMPTY = new LootTable(new LootPool[0]);

    public List<Item> getRandomItems(RandomSource random, LootTableContext context) {
        List<Item> result = new ArrayList<>();
        if (pools != null && context.addVisitedTable(this)) {
            for (LootPool pool : pools) {
                pool.addRandomItems(result, random, context);
            }
            context.removeVisitedTable(this);
        }
        return result;
    }

    public void fill(Inventory container, RandomSource random, LootTableContext context) {
        List<Item> items = getRandomItems(random, context);
        IntList availableSlots = getAvailableSlots(container, random);
        if (availableSlots.isEmpty()) {
            return;
        }
        shuffleAndSplitItems(items, availableSlots.size(), random);
        for (Item item : items) {
            int size = availableSlots.size();
            if (size == 0) {
                return;
            }
            container.setItem(availableSlots.removeInt(size - 1), item);
        }
    }

    private static void shuffleAndSplitItems(List<Item> result, int availableSlots, RandomSource random) {
        List<Item> splittableItems = new ArrayList<>();

        Iterator<Item> iter = result.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            if (!item.isNull()) {
                if (item.getCount() <= 1) {
                    continue;
                }
                splittableItems.add(item);
            }
            iter.remove();
        }

        while (availableSlots - result.size() - splittableItems.size() > 0 && !splittableItems.isEmpty()) {
            Item item = splittableItems.remove(random.nextInt(splittableItems.size()));
            Item copy = item.split(random.nextIntInclusive(1, item.getCount() >> 1));

            if (item.getCount() > 1 && random.nextBoolean()) {
                splittableItems.add(item);
            } else {
                result.add(item);
            }

            if (copy.getCount() > 1 && random.nextBoolean()) {
                splittableItems.add(copy);
            } else {
                result.add(copy);
            }
        }

        result.addAll(splittableItems);
        Utils.shuffle(result, random);
    }

    private static IntList getAvailableSlots(Inventory container, RandomSource random) {
        IntList result = new IntArrayList();
        for (int i = 0; i < container.getSize(); i++) {
            if (container.getItem(i).isNull()) {
                result.add(i);
            }
        }
        Utils.shuffle(result, random);
        return result;
    }
}
