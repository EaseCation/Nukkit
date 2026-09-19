package cn.nukkit.utils;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamReadFeature;
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
    private static final long UNTRUSTED_JSON_MAX_DOCUMENT_LENGTH = 16 * 1024 * 1024;
    private static final long UNTRUSTED_JSON_MAX_TOKEN_COUNT = 100_000;
    private static final int UNTRUSTED_JSON_MAX_NESTING_DEPTH = 64;
    private static final int UNTRUSTED_JSON_MAX_NUMBER_LENGTH = 128;
    private static final int UNTRUSTED_JSON_MAX_STRING_LENGTH = 8 * 1024 * 1024;
    private static final int UNTRUSTED_JSON_MAX_NAME_LENGTH = 1024;

    public static final Indenter PRETTY_INDENTER = new DefaultIndenter("    ", "\n");

    public static final JsonMapper COMMON_JSON_MAPPER = configure(JsonMapper.builder()).build();
    public static final JsonMapper UNTRUSTED_JSON_MAPPER = configure(JsonMapper.builder(
            JsonFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxDocumentLength(UNTRUSTED_JSON_MAX_DOCUMENT_LENGTH)
                            .maxTokenCount(UNTRUSTED_JSON_MAX_TOKEN_COUNT)
                            .maxNestingDepth(UNTRUSTED_JSON_MAX_NESTING_DEPTH)
                            .maxNumberLength(UNTRUSTED_JSON_MAX_NUMBER_LENGTH)
                            .maxStringLength(UNTRUSTED_JSON_MAX_STRING_LENGTH)
                            .maxNameLength(UNTRUSTED_JSON_MAX_NAME_LENGTH)
                            .build())
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                    .build()))
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .disable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();
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
