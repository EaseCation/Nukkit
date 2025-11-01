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
public class SetItemCountFunction extends LootItemFunction {
    private final NumberProvider count;

    @JsonCreator
    public SetItemCountFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("count")
            NumberProvider count
    ) {
        super(conditions);
        this.count = count;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (count != null) {
            item.setCount(count.getInt(random));
        }
        return item;
    }
}
