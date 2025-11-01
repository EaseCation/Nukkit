package cn.nukkit.loot.providers;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

final class ValueProviders {
    static class Deserializer extends StdDeserializer<ValueProvider> {
        Deserializer() {
            super(ValueProvider.class);
        }

        @Override
        public ValueProvider deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            JsonNode node = parser.readValueAsTree();
            if (node.isNumber()) {
                return ValueProvider.constant(node.intValue());
            }
            if (node.isArray() && node.size() >= 2) {
                return ValueProvider.uniform(node.get(0).intValue(), node.get(1).intValue());
            }
            return context.readTreeAsValue(node, IntRange.class);
        }
    }

    private ValueProviders() {
    }
}
