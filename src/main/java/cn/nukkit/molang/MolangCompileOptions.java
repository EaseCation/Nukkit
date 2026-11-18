package cn.nukkit.molang;

import cn.nukkit.molang.runtime.MolangQuerySchema;

import java.util.Objects;

/**
 * 会影响编译结果的语义配置；绑定专用假设只用于固定 runtime 的执行对象。
 */
public record MolangCompileOptions(MolangVersion version, MolangQuerySchema querySchema, boolean stableVariableReads) {
    public static final MolangCompileOptions DEFAULT = new MolangCompileOptions(MolangVersion.LATEST, MolangQuerySchema.EMPTY, false);

    public MolangCompileOptions {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(querySchema, "querySchema");
    }

    public MolangCompileOptions withVersion(MolangVersion version) {
        return new MolangCompileOptions(version, querySchema, stableVariableReads);
    }
}
