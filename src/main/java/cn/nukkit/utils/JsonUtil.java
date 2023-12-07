package cn.nukkit.utils;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class JsonUtil {
    public static final JsonMapper COMMON_JSON_MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .addModule(new Jdk8Module())
            .addModule(new GuavaModule())
            .build();
    public static final JsonMapper TRUSTED_JSON_MAPPER = COMMON_JSON_MAPPER.copy();

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
    }
}
