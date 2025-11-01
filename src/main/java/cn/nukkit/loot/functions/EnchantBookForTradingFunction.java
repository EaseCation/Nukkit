package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString(callSuper = true)
public class EnchantBookForTradingFunction extends LootItemFunction {
    @JsonProperty("base_cost")
    private final int baseCost;
    @JsonProperty("per_level_cost")
    private final int perLevelCost;
    @JsonProperty("base_random_cost")
    private final int baseRandomCost;
    @JsonProperty("per_level_random_cost")
    private final int perLevelRandomCost;

    @Nullable
    private final EnchantmentOption[] enchantments;

    private final transient Optional<IntObjectPair<NumberProvider>[]> parsedEnchantments;

    public EnchantBookForTradingFunction(LootItemCondition[] conditions, EnchantmentOption... enchantments) {
        this(conditions, 2, 3, 5, 10, enchantments);
    }

    public EnchantBookForTradingFunction(
            LootItemCondition[] conditions,
            int baseCost,
            int perLevelCost,
            int baseRandomCost,
            int perLevelRandomCost,
            EnchantmentOption... enchantments
    ) {
        super(conditions);
        this.baseCost = baseCost;
        this.perLevelCost = perLevelCost;
        this.baseRandomCost = baseRandomCost;
        this.perLevelRandomCost = perLevelRandomCost;
        this.enchantments = enchantments;
        List<IntObjectPair<NumberProvider>> parsedEnchantments = new ArrayList<>();
        if (enchantments != null) {
            for (EnchantmentOption enchantment : enchantments) {
                Enchantment enchant = Enchantments.getEnchantmentByIdentifier(enchantment.name);
                if (enchant == null) {
                    continue;
                }
                int min = enchantment.min;
                int max = enchantment.max;
                parsedEnchantments.add(IntObjectPair.of(enchant.getId(),
                        min == max ? NumberProvider.constant(min) : NumberProvider.uniform(min, max)));
            }
            this.parsedEnchantments = Optional.of(parsedEnchantments.toArray(new IntObjectPair[0]));
        } else {
            this.parsedEnchantments = Optional.empty();
        }
    }

    @JsonCreator
    public EnchantBookForTradingFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("base_cost")
            Integer baseCost,
            @JsonProperty("per_level_cost")
            Integer perLevelCost,
            @JsonProperty("base_random_cost")
            Integer baseRandomCost,
            @JsonProperty("per_level_random_cost")
            Integer perLevelRandomCost,
            @JsonProperty("enchantments")
            EnchantmentOption... enchantments
    ) {
        this(
                conditions,
                baseCost == null ? 2 : baseCost,
                perLevelCost == null ? 3 : perLevelCost,
                baseRandomCost == null ? 5 : baseRandomCost,
                perLevelRandomCost == null ? 10 : perLevelRandomCost,
                enchantments
        );
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        Item book = null;
        if (item.is(Item.BOOK)) {
            book = item;
            item = Item.get(Item.ENCHANTED_BOOK, 0, item.getCount());
        }
        if (item.is(Item.ENCHANTED_BOOK)) {
            Item enchantedBook = item;
            Enchantment enchantment = parsedEnchantments.map(enchantments -> {
                if (enchantments.length == 0) {
                    return null;
                }
                IntObjectPair<NumberProvider> option = enchantments[random.nextInt(enchantments.length)];
                Enchantment enchant = Enchantments.get(option.firstInt());
                enchant.setLevel(option.right().getInt(random));
                return enchant;
            }).orElseGet(() -> {
                List<Enchantment> lootable = Enchantments.getLootableEnchantments();
                if (lootable.isEmpty()) {
                    return null;
                }
                Enchantment enchant = lootable.get(random.nextInt(lootable.size())).clone();
                enchant.setLevel(random.nextInt(enchant.getMinLevel(), enchant.getMaxLevel() + 1));
                return enchant;
            });
            enchantedBook.addEnchantment(enchantment);
/*          //TODO: trade table only
            int level = enchantment.getLevel();
            int cost = baseCost + random.nextInt(baseRandomCost + level * perLevelRandomCost) + level * perLevelCost;
            if (enchantment.isTreasureOnly()) {
                cost *= 2;
                if (cost > 64) {
                    cost = 64;
                }
            }
            return cost;
*/
        }
        if (book != null && !item.hasEnchantments()) {
            return book;
        }
        return item;
    }

    public record EnchantmentOption(String name, int min, int max) {
    }
}
