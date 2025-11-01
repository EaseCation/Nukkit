package cn.nukkit.loot.entries;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootPool;
import cn.nukkit.loot.LootTable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.List;

@JsonDeserialize(using = LootTableEntry.Deserializer.class)
public record LootTableEntry(LootTable table) {
    public boolean createItem(List<Item> output, RandomSource random, LootTableContext context) {
        if (table == null) {
            return false;
        }
        List<Item> items = table.getRandomItems(random, context);
        if (items.isEmpty()) {
            return false;
        }
        output.addAll(items);
        return true;
    }

    static class Deserializer extends StdDeserializer<LootTableEntry> {
        Deserializer() {
            super(LootTableEntry.class);
        }

        @Override
        public LootTableEntry deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            return new LootTableEntry(new LootTable(parser.readValueAs(LootPool[].class)));
        }
    }
}
