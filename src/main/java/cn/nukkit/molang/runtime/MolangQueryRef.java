package cn.nukkit.molang.runtime;

import java.util.Locale;

/**
 * 一个编译程序内的查询槽位；运行时绑定时按名称解析一次，调用时按槽位分派。
 */
public record MolangQueryRef(String name, int bindingIndex, MolangQueryDefinition definition, boolean foreignContext) {
    public MolangQueryRef(String name, int bindingIndex, MolangQueryDefinition definition) {
        this(name, bindingIndex, definition, false);
    }

    public MolangQueryRef {
        name = name.toLowerCase(Locale.ROOT);
        if (bindingIndex < 0 || !name.equals(definition.name())) {
            throw new IllegalArgumentException("Invalid Molang query reference: " + name);
        }
    }
}
