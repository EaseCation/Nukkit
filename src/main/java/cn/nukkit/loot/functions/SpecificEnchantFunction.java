package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.ValueProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

@ToString(callSuper = true)
public class SpecificEnchantFunction extends LootItemFunction {
    private final EnchantInfo[] enchants;

    @JsonCreator
    public SpecificEnchantFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("enchants")
            EnchantInfo... enchants
    ) {
        super(conditions);
        this.enchants = enchants;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        Item book = null;
        if (item.is(Item.BOOK)) {
            book = item;
            item = Item.get(Item.ENCHANTED_BOOK, 0, item.getCount());
        }
        if (enchants != null) {
            ENCHANT:
            for (EnchantInfo enchant : enchants) {
                if (enchant == null || enchant.id == -1) {
                    continue;
                }
                Enchantment enchantment = Enchantments.get(enchant.id);
                if (enchantment == null) {
                    continue;
                }
                if (!item.is(Item.ENCHANTED_BOOK)) {
                    for (Enchantment ench : item.getEnchantments()) {
                        if (!enchantment.isCompatibleWith(ench)) {
                            continue ENCHANT;
                        }
                    }
                }
                enchantment.setLevel(enchant.level != null ? enchant.level.getValue(random) : 1);
                item.addEnchantment(enchantment);
            }
        }
        if (book != null && !item.hasEnchantments()) {
            return book;
        }
        return item;
    }

    @JsonDeserialize(using = EnchantInfo.Deserializer.class)
    public record EnchantInfo(
            int id,
            ValueProvider level
    ) {
        static class Deserializer extends StdDeserializer<EnchantInfo> {
            Deserializer() {
                super(EnchantInfo.class);
            }

            @Override
            public EnchantInfo deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
                JsonNode node = parser.readValueAsTree();
                if (node.isString()) {
                    Enchantment enchant = Enchantments.getEnchantmentByIdentifier(node.asString());
                    return new EnchantInfo(enchant != null ? enchant.getId() : -1, null);
                }

                JsonNode idNode = node.get("id");
                JsonNode levelNode = node.get("level");
                Enchantment enchant = idNode != null ? Enchantments.getEnchantmentByIdentifier(idNode.asString()) : null;
                return new EnchantInfo(
                        enchant != null ? enchant.getId() : -1,
                        levelNode != null ? context.readTreeAsValue(levelNode, ValueProvider.class).initWithDefaultValue(1, 1) : null
                );
            }
        }
    }
}
