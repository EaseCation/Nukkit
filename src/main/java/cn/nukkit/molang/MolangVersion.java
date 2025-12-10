package cn.nukkit.molang;

import cn.nukkit.utils.SemVersion;

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
}
