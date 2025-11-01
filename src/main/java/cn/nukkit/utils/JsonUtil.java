package cn.nukkit.utils;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.core.util.DefaultPrettyPrinter.Indenter;
import tools.jackson.core.util.Separators;
import tools.jackson.core.util.Separators.Spacing;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.guava.GuavaModule;

public class JsonUtil {
    public static final Indenter PRETTY_INDENTER = new DefaultIndenter("    ", "\n");

    public static final JsonMapper COMMON_JSON_MAPPER = configure(JsonMapper.builder()).build();
    public static final JsonMapper TRUSTED_JSON_MAPPER = configure(JsonMapper.builder(
            JsonFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(Integer.MAX_VALUE)
                            .maxNumberLength(Integer.MAX_VALUE)
                            .maxStringLength(Integer.MAX_VALUE)
                            .maxNameLength(Integer.MAX_VALUE)
                            .maxDocumentLength(0) // unlimited
                            .build())
                    .streamWriteConstraints(StreamWriteConstraints.builder()
                            .maxNestingDepth(Integer.MAX_VALUE)
                            .build())
                    .build()))
            .build();
    public static final JsonMapper PRETTY_JSON_MAPPER = TRUSTED_JSON_MAPPER.rebuild()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private static JsonMapper.Builder configure(JsonMapper.Builder builder) {
        return builder
                .addModule(new GuavaModule())
                .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .defaultPrettyPrinter(new DefaultPrettyPrinter(new Separators()
                        .withObjectNameValueSpacing(Spacing.AFTER)
                        .withArrayEmptySeparator("")
                        .withObjectEmptySeparator(""))
                        .withArrayIndenter(PRETTY_INDENTER)
                        .withObjectIndenter(PRETTY_INDENTER));
    }
}
