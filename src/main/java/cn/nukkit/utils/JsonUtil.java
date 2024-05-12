package cn.nukkit.utils;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.core.util.Separators.Spacing;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class JsonUtil {
    public static final Indenter PRETTY_INDENTER = new DefaultIndenter("    ", "\n");

    public static final JsonMapper COMMON_JSON_MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .addModule(new Jdk8Module())
            .addModule(new GuavaModule())
            .defaultPrettyPrinter(new DefaultPrettyPrinter(new Separators()
                    .withObjectFieldValueSpacing(Spacing.AFTER)
                    .withArrayEmptySeparator("")
                    .withObjectEmptySeparator(""))
                    .withArrayIndenter(PRETTY_INDENTER)
                    .withObjectIndenter(PRETTY_INDENTER))
            .build();
    public static final JsonMapper TRUSTED_JSON_MAPPER = COMMON_JSON_MAPPER.copy();
    public static final ObjectMapper PRETTY_JSON_MAPPER;

    static {
        TRUSTED_JSON_MAPPER.getFactory()
                .setStreamReadConstraints(StreamReadConstraints.builder()
                        .maxNestingDepth(Integer.MAX_VALUE)
                        .maxNumberLength(Integer.MAX_VALUE)
                        .maxStringLength(Integer.MAX_VALUE)
                        .maxNameLength(Integer.MAX_VALUE)
                        .maxDocumentLength(0) // unlimited
                        .build())
                .setStreamWriteConstraints(StreamWriteConstraints.builder()
                        .maxNestingDepth(Integer.MAX_VALUE)
                        .build());
        PRETTY_JSON_MAPPER = TRUSTED_JSON_MAPPER.copy()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }
}
