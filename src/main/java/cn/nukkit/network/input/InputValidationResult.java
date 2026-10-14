package cn.nukkit.network.input;

/** 验证是否允许提交，以及模拟器是否给出了本帧起跳结果。 */
public enum InputValidationResult {
    REJECTED(false, false, false),
    ACCEPTED(true, false, false),
    ACCEPTED_MOVEMENT(true, true, false),
    ACCEPTED_GROUND_JUMP(true, true, true);

    private final boolean accepted;
    private final boolean groundJumpKnown;
    private final boolean groundJump;

    InputValidationResult(boolean accepted, boolean groundJumpKnown, boolean groundJump) {
        this.accepted = accepted;
        this.groundJumpKnown = groundJumpKnown;
        this.groundJump = groundJump;
    }

    public boolean accepted() {
        return this.accepted;
    }

    public boolean groundJumpKnown() {
        return this.groundJumpKnown;
    }

    public boolean groundJump() {
        return this.groundJump;
    }

    public InputValidationResult withoutGroundJump() {
        return this == ACCEPTED_GROUND_JUMP ? ACCEPTED_MOVEMENT : this;
    }
}
