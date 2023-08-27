package cn.nukkit.command.data;

public enum CommandFlag {
    /**
     * Usage flag
     */
    TEST, // default = NORMAL
    /**
     * Visibility flag
     */
    HIDDEN_FROM_COMMAND_BLOCK_ORIGIN, // default = VISIBLE
    /**
     * Visibility flag
     */
    HIDDEN_FROM_PLAYER_ORIGIN, // HIDDEN_FROM_PLAYER_ORIGIN | HIDDEN_FROM_COMMAND_BLOCK_ORIGIN = HIDDEN
    /**
     * Sync flag
     */
    LOCAL, // default = SYNCED
    /**
     * Execute flag
     */
    DISALLOWED, // default = ALLOWED
    /**
     * Type flag
     */
    MESSAGE, // default = NONE
    /**
     * Cheat flag
     */
    NOT_CHEAT, // default = CHEAT
}
