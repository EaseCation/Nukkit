package cn.nukkit.command.data;

/**
 * Canonical lowercase integer postfixes used by vanilla Bedrock commands.
 *
 * <p>Use these values with {@link CommandParameter#newPostfix(String, String)} to describe
 * command parameters. The command implementation remains responsible for parsing the suffix
 * and applying its meaning or converting duration units.</p>
 */
public interface CommandPostfixes {
    /**
     * Experience levels in the vanilla {@code /xp} level overload, for example {@code 5l}.
     */
    String L = "l";

    /**
     * Game ticks in vanilla {@code /schedule delay add}, for example {@code 20t}.
     */
    String T = "t";

    /**
     * Seconds in vanilla {@code /schedule delay add}, for example {@code 5s}.
     */
    String S = "s";

    /**
     * In-game days in vanilla {@code /schedule delay add}, for example {@code 1d}.
     */
    String D = "d";
}
