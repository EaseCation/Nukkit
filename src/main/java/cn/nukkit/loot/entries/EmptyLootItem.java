package cn.nukkit.loot.entries;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmptyLootItem extends LootPoolEntry {
    public EmptyLootItem(int weight, int quality, LootItemCondition[] conditions, LootTableEntry pools) {
        super(weight, quality, conditions, pools);
    }

    @JsonCreator
    public EmptyLootItem(
            @JsonProperty("weight")
            Integer weight,
            @JsonProperty("quality")
            Integer quality,
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("pools")
            LootTableEntry subTable) {
        this(
                weight == null ? 1 : weight,
                quality == null ? 0 : quality,
                conditions,
                subTable
        );
    }

    @Override
    protected boolean createItemInternal(List<Item> output, RandomSource random, LootTableContext context) {
        return false;
    }
}
