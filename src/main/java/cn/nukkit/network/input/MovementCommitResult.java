package cn.nukkit.network.input;

/** 位置提交结果不代表整个输入包或其所有动作都已完成。 */
public enum MovementCommitResult {
    NO_PENDING,
    UNCHANGED,
    APPLIED,
    REJECTED,
    INVALIDATED
}
