package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class ExplorationMapFunction extends LootItemFunction {
    private final String destination;

    private final transient int parsedAuxDataValue;

    @JsonCreator
    public ExplorationMapFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("destination")
            String destination
    ) {
        super(conditions);
        this.destination = destination;
        parsedAuxDataValue = ItemMap.getDataByDestination(destination);
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (parsedAuxDataValue != 0 && (item.is(Item.FILLED_MAP) || item.is(Item.EMPTY_MAP))) {
            item = Item.get(Item.FILLED_MAP, parsedAuxDataValue, item.getCount());
            //TODO: fill map
        }
        return item;
    }
}
