package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * 不可变查询语义签名，用于绑定版编译及有界编译缓存的身份判断。
 */
public record MolangQuerySchema(Map<String, MolangQueryDefinition> definitions) {
    public static final MolangQuerySchema EMPTY = new MolangQuerySchema(Map.of());

    public MolangQuerySchema {
        definitions = Map.copyOf(definitions);
        for (Map.Entry<String, MolangQueryDefinition> entry : definitions.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().name())) {
                throw new IllegalArgumentException("Query schema key does not match its definition: " + entry.getKey());
            }
        }
    }

    /**
     * 内建宿主的查询表在初始化后固定，同一组预期查询只需在切换合同时重新核对。
     */
    public void validate(MolangRuntime runtime) {
        MolangQuerySchema actual = runtime.querySchema();
        if (this == actual) {
            return;
        }
        if (runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner) {
            if (this != owner.validatedSchema) {
                validateDefinitions(actual);
                owner.validatedSchema = this;
            }
        } else {
            validateDefinitions(actual);
        }
    }

    private void validateDefinitions(MolangQuerySchema actual) {
        for (MolangQueryDefinition expected : definitions.values()) {
            if (!expected.equals(actual.definitions.get(expected.name()))) {
                throw new IllegalArgumentException("Runtime query contract differs from compiled schema: " + expected.name());
            }
        }
    }

    @Nullable
    public MolangQueryDefinition get(String name) {
        return definitions.get(name.toLowerCase(Locale.ROOT));
    }
}
