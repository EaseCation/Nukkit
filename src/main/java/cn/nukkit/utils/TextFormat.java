package cn.nukkit.utils;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import java.util.regex.Pattern;

/**
 * All supported formatting values for chat and console.
 */
public enum TextFormat {
    // Color codes
    /**
     * Represents black.
     */
    BLACK('0'),
    /**
     * Represents dark blue.
     */
    DARK_BLUE('1'),
    /**
     * Represents dark green.
     */
    DARK_GREEN('2'),
    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA('3'),
    /**
     * Represents dark red.
     */
    DARK_RED('4'),
    /**
     * Represents dark purple.
     */
    DARK_PURPLE('5'),
    /**
     * Represents gold.
     */
    GOLD('6'),
    /**
     * Represents gray.
     */
    GRAY('7'),
    /**
     * Represents dark gray.
     */
    DARK_GRAY('8'),
    /**
     * Represents blue.
     */
    BLUE('9'),
    /**
     * Represents green.
     */
    GREEN('a'),
    /**
     * Represents aqua.
     */
    AQUA('b'),
    /**
     * Represents red.
     */
    RED('c'),
    /**
     * Represents light purple.
     */
    LIGHT_PURPLE('d'),
    /**
     * Represents yellow.
     */
    YELLOW('e'),
    /**
     * Represents white.
     */
    WHITE('f'),

    // Color codes (Bedrock only)
    /**
     * Represents minecoin gold.
     */
    MINECOIN_GOLD('g'),
    /**
     * Represents material quartz.
     * @since 1.19.80
     */
    MATERIAL_QUARTZ('h'),
    /**
     * Represents material iron.
     * @since 1.19.80
     */
    MATERIAL_IRON('i'),
    /**
     * Represents material netherite.
     * @since 1.19.80
     */
    MATERIAL_NETHERITE('j'),
    /**
     * Represents material redstone.
     * @since 1.19.80
     */
    MATERIAL_REDSTONE('m'),
    /**
     * Represents material copper.
     * @since 1.19.80
     */
    MATERIAL_COPPER('n'),
    /**
     * Represents material gold.
     * @since 1.19.80
     */
    MATERIAL_GOLD('p'),
    /**
     * Represents material emerald.
     * @since 1.19.80
     */
    MATERIAL_EMERALD('q'),
    /**
     * Represents material diamond.
     * @since 1.19.80
     */
    MATERIAL_DIAMOND('s'),
    /**
     * Represents material lapis.
     * @since 1.19.80
     */
    MATERIAL_LAPIS('t'),
    /**
     * Represents material amethyst.
     * @since 1.19.80
     */
    MATERIAL_AMETHYST('u'),

    // Formatting codes
    /**
     * Makes the text obfuscated.
     */
    OBFUSCATED('k', true),
    /**
     * Makes the text bold.
     */
    BOLD('l', true),
    /**
     * Makes the text italic.
     */
    ITALIC('o', true),
    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r');

    /**
     * The special character which prefixes all format codes. Use this if
     * you need to dynamically convert format codes from your custom format.
     */
    public static final char ESCAPE = '\u00A7';

    private static final Pattern CLEAN_PATTERN = Pattern.compile("(?i)" + ESCAPE + "[0-9A-U]");
    private final static Char2ObjectMap<TextFormat> BY_CHAR = new Char2ObjectOpenHashMap<>();

    static {
        for (TextFormat color : values()) {
            BY_CHAR.put(color.code, color);
        }
    }

    private final char code;
    private final boolean isFormat;
    private final String toString;

    TextFormat(char code) {
        this(code, false);
    }

    TextFormat(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{ESCAPE, code});
    }

    /**
     * Gets the TextFormat represented by the specified format code.
     *
     * @param code Code to check
     * @return Associative {@link TextFormat} with the given code,
     * or null if it doesn't exist
     */
    public static TextFormat getByChar(char code) {
        return BY_CHAR.get(code);
    }

    /**
     * Gets the TextFormat represented by the specified format code.
     *
     * @param code Code to check
     * @return Associative {@link TextFormat} with the given code,
     * or null if it doesn't exist
     */
    public static TextFormat getByChar(String code) {
        if (code == null || code.length() <= 1) {
            return null;
        }

        return BY_CHAR.get(code.charAt(0));
    }

    /**
     * Cleans the given message of all format codes.
     *
     * @param input String to clean.
     * @return A copy of the input string, without any formatting.
     */
    public static String clean(final String input) {
        return clean(input, false);
    }

    public static String clean(final String input, final boolean recursive) {
        if (input == null) {
            return null;
        }

        String result = CLEAN_PATTERN.matcher(cleanIcon(input, recursive)).replaceAll("");

        if (recursive && CLEAN_PATTERN.matcher(result).find()) {
            return clean(result, true);
        }
        return result;
    }

    /**
     * Cleans the given message of all magic characters.
     *
     * @param input String to clean.
     * @return A copy of the input string, without any icons.
     */
    public static String cleanIcon(String input) {
        return cleanIcon(input, false);
    }

    public static String cleanIcon(String input, boolean recursive) {
        if (input == null) {
            return null;
        }

        String result = CharacterIcon.CLEAN_PATTERN.matcher(input).replaceAll("");

        if (recursive &&  CharacterIcon.CLEAN_PATTERN.matcher(result).find()) {
            return cleanIcon(result, true);
        }
        return result;
    }

    /**
     * Translates a string using an alternate format code character into a
     * string that uses the internal TextFormat.ESCAPE format code
     * character. The alternate format code character will only be replaced if
     * it is immediately followed by 0-9, A-G, a-g, K-O, k-o, R or r.
     *
     * @param altFormatChar   The alternate format code character to replace. Ex: &amp;
     * @param textToTranslate Text containing the alternate format code character.
     * @return Text containing the TextFormat.ESCAPE format code character.
     */
    public static String colorize(char altFormatChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            int x = i + 1;
            if (b[i] == altFormatChar && "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUu".indexOf(b[x]) > -1) {
                b[i] = TextFormat.ESCAPE;
                b[x] = Character.toLowerCase(b[x]);
            }
        }
        return new String(b);
    }

    /**
     * Translates a string, using an ampersand (&amp;) as an alternate format code
     * character, into a string that uses the internal TextFormat.ESCAPE format
     * code character. The alternate format code character will only be replaced if
     * it is immediately followed by 0-9, A-G, a-g, K-O, k-o, R or r.
     *
     * @param textToTranslate Text containing the alternate format code character.
     * @return Text containing the TextFormat.ESCAPE format code character.
     */
    public static String colorize(String textToTranslate) {
        return colorize('&', textToTranslate);
    }

    /**
     * Gets the chat color used at the end of the given input string.
     *
     * @param input Input string to retrieve the colors from.
     * @return Any remaining chat color to pass onto the next line.
     */
    public static String getLastColors(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ESCAPE && index < length - 1) {
                char c = input.charAt(index + 1);
                TextFormat color = getByChar(c);

                if (color != null) {
                    result.insert(0, color);

                    // Once we find a color or reset we can stop searching
                    if (color.isColor() || color == RESET) {
                        break;
                    }
                }
            }
        }

        return result.toString();
    }

    /**
     * Gets the char value associated with this color
     *
     * @return A char value of this color code
     */
    public char getChar() {
        return code;
    }

    @Override
    public String toString() {
        return toString;
    }

    /**
     * Checks if this code is a format code as opposed to a color code.
     */
    public boolean isFormat() {
        return isFormat;
    }

    /**
     * Checks if this code is a color code as opposed to a format code.
     */
    public boolean isColor() {
        return !isFormat && this != RESET;
    }
}