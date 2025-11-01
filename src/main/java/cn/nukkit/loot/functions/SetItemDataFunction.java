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
public class SetItemDataFunction extends LootItemFunction {
    private final NumberProvider data;

    @JsonCreator
    public SetItemDataFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("data")
            NumberProvider data
    ) {
        super(conditions);
        this.data = data;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (data != null) {
            item.setDamage(data.getInt(random));
        }
        return item;
    }
}
