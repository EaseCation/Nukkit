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

    @Nullable
    public MolangQueryDefinition get(String name) {
        return definitions.get(name.toLowerCase(Locale.ROOT));
    }
}
