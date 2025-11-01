package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class EnchantRandomlyFunction extends LootItemFunction {
    private final boolean treasure;

    public EnchantRandomlyFunction(LootItemCondition[] conditions) {
        this(conditions, false);
    }

    @JsonCreator
    public EnchantRandomlyFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("treasure")
            boolean treasure
    ) {
        super(conditions);
        this.treasure = treasure;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        Item book = null;
        if (item.is(Item.BOOK)) {
            book = item;
            item = Item.get(Item.ENCHANTED_BOOK, 0, item.getCount());
        }
        EnchantmentHelper.randomlyEnchant(random, item, random.nextInt(30), treasure);
        if (book != null && !item.hasEnchantments()) {
            return book;
        }
        return item;
    }
}
