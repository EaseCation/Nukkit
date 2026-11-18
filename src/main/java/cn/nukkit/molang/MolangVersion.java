package cn.nukkit.molang;

import cn.nukkit.utils.SemVersion;

import java.util.Objects;

public enum MolangVersion {
    BEFORE_VERSIONING(new SemVersion(0, 0, 0)),
    INITIAL(new SemVersion(1, 17, 0)),
    FIXED_ITEM_REMAINING_USE_DURATION_QUERY(new SemVersion(1, 17, 30)),
    EXPRESSION_ERROR_MESSAGES(new SemVersion(1, 17, 40)),
    UNEXPECTED_OPERATOR_ERRORS(new SemVersion(1, 17, 40)),
    CONDITIONAL_OPERATOR_ASSOCIATIVITY(new SemVersion(1, 18, 10)),
    COMPARISON_AND_LOGICAL_OPERATOR_PRECEDENCE(new SemVersion(1, 18, 20)),
    DIVIDE_BY_NEGATIVE_VALUE(new SemVersion(1, 19, 60)),
    FIXED_CAPE_FLAP_AMOUNT_QUERY(new SemVersion(1, 20, 0)),
    QUERY_BLOCK_PROPERTY_RENAMED_TO_STATE(new SemVersion(1, 20, 10)),
    DEPRECATE_OLD_BLOCK_QUERY_NAMES(new SemVersion(1, 20, 40)),
    DEPRECATED_SNIFFER_AND_CAMEL_QUERIES(new SemVersion(1, 20, 50)),
    LEAF_SUPPORTING_IN_FIRST_SOLID_BLOCK_BELOW(new SemVersion(1, 20, 70)),
    LATEST(new SemVersion(1, 20, 70)),
    ;

    private final SemVersion minEngineVersion;

    MolangVersion(SemVersion minEngineVersion) {
        this.minEngineVersion = minEngineVersion;
    }

    public SemVersion getMinEngineVersion() {
        return minEngineVersion;
    }

    /**
     * 按资源包最低引擎版本选择已知的语义版本；未来版本使用目前最新的已知规则。
     */
    public static MolangVersion forMinEngineVersion(SemVersion version) {
        Objects.requireNonNull(version, "version");
        if (version.getMajor() < 0 || version.getMinor() < 0 || version.getPatch() < 0) {
            throw new IllegalArgumentException("Molang engine version components must be non-negative");
        }
        MolangVersion selected = BEFORE_VERSIONING;
        for (MolangVersion candidate : values()) {
            if (compare(version, candidate.minEngineVersion) >= 0) {
                selected = candidate;
            }
        }
        return selected;
    }

    public boolean supports(MolangVersion change) {
        return compare(minEngineVersion, Objects.requireNonNull(change, "change").minEngineVersion) >= 0;
    }

    private static int compare(SemVersion left, SemVersion right) {
        int major = Integer.compare(left.getMajor(), right.getMajor());
        if (major != 0) {
            return major;
        }
        int minor = Integer.compare(left.getMinor(), right.getMinor());
        return minor != 0 ? minor : Integer.compare(left.getPatch(), right.getPatch());
    }
}
