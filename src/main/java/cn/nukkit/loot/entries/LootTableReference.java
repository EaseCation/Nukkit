package cn.nukkit.loot.entries;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.LootTables;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.List;
import java.util.Optional;

@JsonDeserialize(using = LootTableReference.Deserializer.class)
@ToString(callSuper = true)
public class LootTableReference extends LootPoolEntry {
    @JsonProperty("name")
    private final String lootTableName;
    private transient Optional<LootTable> lootTable;

    public LootTableReference(int weight, int quality, LootItemCondition[] conditions, LootTableEntry pools, String lootTableName) {
        super(weight, quality, conditions, pools);
        this.lootTableName = lootTableName;
    }

    @Override
    protected boolean createItemInternal(List<Item> output, RandomSource random, LootTableContext context) {
        if (lootTable == null) {
            lootTable = Optional.ofNullable(LootTables.lookupByName(lootTableName));
        }
        if (lootTable.isEmpty()) {
            return false;
        }
        LootTable table = lootTable.get();
        List<Item> items = table.getRandomItems(random, context);
        if (items.isEmpty()) {
            return false;
        }
        output.addAll(items);
        return true;
    }

    static class Deserializer extends StdDeserializer<LootTableReference> {
        Deserializer() {
            super(LootTableReference.class);
        }

        @Override
        public LootTableReference deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            JsonNode node = parser.readValueAsTree();
            JsonNode weightNode = node.get("weight");
            JsonNode qualityNode = node.get("quality");
            JsonNode conditionsNode = node.get("conditions");
            JsonNode poolsNode = node.get("pools");
            JsonNode nameNode = node.get("name");

            return new LootTableReference(
                    weightNode != null ? weightNode.intValue() : 1,
                    qualityNode != null ? qualityNode.intValue() : 0,
                    conditionsNode != null ? context.readTreeAsValue(conditionsNode, LootItemCondition[].class) : null,
                    poolsNode != null ? context.readTreeAsValue(poolsNode, LootTableEntry.class) : null,
                    nameNode != null ? nameNode.asString() : null
            );
        }
    }
}
