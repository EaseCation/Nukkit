package cn.nukkit.item.randomitem;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.math.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.item.randomitem in project nukkit.
 */
public class ConstantItemSelector extends Selector {

    protected final Item item;

    @Nullable
    protected final List<Consumer<Item>> functions = new ArrayList<>();

    public ConstantItemSelector(int id, Selector parent) {
        this(id, 0, parent);
    }

    public ConstantItemSelector(int id, int meta, Selector parent) {
        this(id, meta, 1, parent);
    }

    public ConstantItemSelector(int id, int meta, int count, Selector parent) {
        this(Item.get(id, meta, count), parent);
    }

    public ConstantItemSelector(Item item, Selector parent) {
        super(parent);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Object select() {
        Item item = getItem().clone();

        if (!functions.isEmpty()) {
            for (Consumer<Item> function : functions) {
                function.accept(item);
            }
        }

        return item;
    }

    private void addFunction(Consumer<Item> function) {
        functions.add(function);
    }

    public ConstantItemSelector setCount(int min, int max) {
        addFunction(item -> item.setCount(ThreadLocalRandom.current().nextInt(min, max + 1)));
        return this;
    }

    public ConstantItemSelector randomAuxValue(int min, int max) {
        addFunction(item -> item.setDamage(ThreadLocalRandom.current().nextInt(min, max + 1)));
        return this;
    }

    public ConstantItemSelector setDamage(float min, float max) {
        addFunction(item -> item.setDamage(Mth.floor(item.getMaxDurability() * (1 - ThreadLocalRandom.current().nextFloat(min, max)))));
        return this;
    }

    public ConstantItemSelector enchantRandomly() {
        return enchantRandomly(false);
    }

    public ConstantItemSelector enchantRandomly(boolean treasure) {
        addFunction(item -> EnchantmentHelper.randomlyEnchant(item, ThreadLocalRandom.current().nextInt(0, 30), treasure));
        return this;
    }

    public ConstantItemSelector enchantWithLevels(int levels) {
        return enchantWithLevels(levels, false);
    }

    public ConstantItemSelector enchantWithLevels(int levels, boolean treasure) {
        addFunction(item -> EnchantmentHelper.randomlyEnchant(item, levels, treasure));
        return this;
    }
}
