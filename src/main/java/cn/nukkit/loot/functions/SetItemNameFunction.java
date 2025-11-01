package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString(callSuper = true)
public class SetItemNameFunction extends LootItemFunction {
    @Nullable
    private final String name;

    @JsonCreator
    public SetItemNameFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("name")
            String name
    ) {
        super(conditions);
        this.name = name;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        item.setCustomName(name);
        return item;
    }
}
