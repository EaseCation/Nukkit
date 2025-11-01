package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class EnchantWithLevelsFunction extends LootItemFunction {
    private final NumberProvider levels;
    private final boolean treasure;

    public EnchantWithLevelsFunction(LootItemCondition[] conditions, NumberProvider levels) {
        this(conditions, levels, false);
    }

    @JsonCreator
    public EnchantWithLevelsFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("levels")
            NumberProvider levels,
            @JsonProperty("treasure")
            boolean treasure
    ) {
        super(conditions);
        this.levels = levels;
        this.treasure = treasure;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        Item book = null;
        if (item.is(Item.BOOK)) {
            book = item;
            item = Item.get(Item.ENCHANTED_BOOK, 0, item.getCount());
        }
        EnchantmentHelper.randomlyEnchant(random, item, levels != null ? levels.getInt(random) : 0, treasure);
        if (book != null && !item.hasEnchantments()) {
            return book;
        }
        return item;
    }
}
