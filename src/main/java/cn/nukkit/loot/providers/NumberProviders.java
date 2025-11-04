package cn.nukkit.loot.providers;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

final class NumberProviders {
    static class Deserializer extends StdDeserializer<NumberProvider> {
        Deserializer() {
            super(NumberProvider.class);
        }

        @Override
        public NumberProvider deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            JsonNode node = parser.readValueAsTree();
            if (node.isNumber()) {
                return NumberProvider.constant(node.floatValue());
            }
            if (node.isArray() && node.size() >= 2) {
                return NumberProvider.uniform(node.get(0).floatValue(), node.get(1).floatValue());
            }
            return context.readTreeAsValue(node, RandomValueBounds.class);
        }
    }

    private NumberProviders() {
    }
}
