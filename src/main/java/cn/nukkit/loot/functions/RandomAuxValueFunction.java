package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class RandomAuxValueFunction extends LootItemFunction {
    private final NumberProvider values;

    @JsonCreator
    public RandomAuxValueFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("values")
            NumberProvider values) {
        super(conditions);
        this.values = values;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (values != null) {
            item.setDamage(values.getInt(random));
        }
        return item;
    }
}
