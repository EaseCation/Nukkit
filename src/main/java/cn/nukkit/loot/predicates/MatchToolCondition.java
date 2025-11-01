package cn.nukkit.loot.predicates;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.providers.ValueProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

public record MatchToolCondition(
        ValueProvider count,
        ValueProvider durability,
        EnchantmentInfo[] enchantments,
        String item,
        @JsonProperty("minecraft:match_tool_filter_any")
        String[] itemTagsAny,
        @JsonProperty("minecraft:match_tool_filter_all")
        String[] itemTagsAll,
        @JsonProperty("minecraft:match_tool_filter_none")
        String[] itemTagsNone
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        Item tool = context.getTool();
        if (tool == null) {
            return false;
        }
        if (item != null && tool.getId() != Items.getIdByName(item, true, true)) {
            return false;
        }
        if (count != null && !count.isInRangeInclusive(tool.getCount())) {
            return false;
        }
        if (durability != null && tool instanceof ItemDurable && !durability.isInRangeInclusive(tool.getMaxDurability() - tool.getDamage() + 1)) {
            return false;
        }
        if (enchantments != null) {
            for (EnchantmentInfo enchantment : enchantments) {
                if (enchantment == null || enchantment.enchantment == -1) {
                    continue;
                }
                if (!tool.hasEnchantment(enchantment.enchantment)
                        || enchantment.levels != null && !enchantment.levels.isInRangeInclusive(tool.getEnchantmentLevel(enchantment.enchantment))) {
                    return false;
                }
            }
        }
/*      //TODO: match item tags
        if (itemTagsAny != null) {
            boolean found = false;
            for (String tag : itemTagsAny) {
                if (item.hasItemTag(tag)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        if (itemTagsAll != null) {
            for (String tag : itemTagsAll) {
                if (!item.hasItemTag(tag)) {
                    return false;
                }
            }
        }
        if (itemTagsNone != null) {
            for (String tag : itemTagsNone) {
                if (item.hasItemTag(tag)) {
                    return false;
                }
            }
        }
*/
        return false;
    }

    @JsonDeserialize(using = EnchantmentInfo.Deserializer.class)
    public record EnchantmentInfo(
            int enchantment,
            ValueProvider levels
    ) {
        static class Deserializer extends StdDeserializer<EnchantmentInfo> {
            Deserializer() {
                super(EnchantmentInfo.class);
            }

            @Override
            public EnchantmentInfo deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
                JsonNode node = parser.readValueAsTree();
                if (node.isString()) {
                    Enchantment enchant = Enchantments.getEnchantmentByIdentifier(node.asString());
                    return new EnchantmentInfo(enchant != null ? enchant.getId() : -1, null);
                }

                JsonNode idNode = node.get("id");
                JsonNode levelNode = node.get("level");
                Enchantment enchant = idNode != null ? Enchantments.getEnchantmentByIdentifier(idNode.asString()) : null;
                return new EnchantmentInfo(
                        enchant != null ? enchant.getId() : -1,
                        levelNode != null ? context.readTreeAsValue(levelNode, ValueProvider.class) : null
                );
            }
        }
    }
}
