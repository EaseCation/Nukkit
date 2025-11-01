package cn.nukkit.loot.entries;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.functions.LootItemFunction;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.predicates.LootItemConditions;
import cn.nukkit.math.RandomSource;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import javax.annotation.Nullable;
import java.util.List;

@Log4j2
@JsonDeserialize(using = LootItem.Deserializer.class)
@ToString(callSuper = true)
public class LootItem extends LootPoolEntry {
    private final transient Item item;
    @Nullable
    private final LootItemFunction[] functions;

    public LootItem(int weight, int quality, LootItemCondition[] conditions, LootTableEntry pools, Item item, LootItemFunction[] functions) {
        super(weight, quality, conditions, pools);
        this.item = item;
        this.functions = functions;
    }

    @Override
    protected boolean createItemInternal(List<Item> output, RandomSource random, LootTableContext context) {
        if (item == null || item.isNull()) {
            return false;
        }

        Item item = this.item.clone();
        if (functions != null) {
            for (LootItemFunction function : functions) {
                if (!LootItemConditions.allApply(function.getConditions(), random, context)) {
                    continue;
                }
                item = function.apply(item, random, context);
                if (item.getCount() <= 0) {
                    return false;
                }
            }
        }

        int count = item.getCount();
        int maxSize = item.getMaxStackSize();
        while (count > maxSize) {
            Item copy = item.split(maxSize);
            count -= maxSize;
            output.add(copy);
        }
        output.add(item);
        return true;
    }

    static class Deserializer extends StdDeserializer<LootItem> {
        Deserializer() {
            super(LootItem.class);
        }

        @Override
        public LootItem deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            JsonNode node = parser.readValueAsTree();
            JsonNode weightNode = node.get("weight");
            JsonNode qualityNode = node.get("quality");
            JsonNode conditionsNode = node.get("conditions");
            JsonNode poolsNode = node.get("pools");
            JsonNode nameNode = node.get("name");
            JsonNode functionsNode = node.get("functions");

            Item item;
            if (nameNode != null) {
                String name = nameNode.asString();
                item = Item.fromStringNullable(name, true);
                if (item == null) {
                    log.error("Unknown loot item: {}", name);
                }
            } else {
                item = null;
            }

            return new LootItem(
                    weightNode != null ? weightNode.intValue() : 1,
                    qualityNode != null ? qualityNode.intValue() : 0,
                    conditionsNode != null ? context.readTreeAsValue(conditionsNode, LootItemCondition[].class) : null,
                    poolsNode != null ? context.readTreeAsValue(poolsNode, LootTableEntry.class) : null,
                    item,
                    functionsNode != null ? context.readTreeAsValue(functionsNode, LootItemFunction[].class) : null
            );
        }
    }
}
