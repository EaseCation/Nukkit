package cn.nukkit.command.data;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class CommandParameter {

    public String name;
    public CommandParamType type;
    public boolean optional;
    public final Set<CommandParamOption> options = EnumSet.noneOf(CommandParamOption.class);

    public CommandEnum enumData;
    public String postFix;

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated
    public CommandParameter(String name, String type, boolean optional) {
        this(name, fromString(type), optional);
    }

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated
    public CommandParameter(String name, CommandParamType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated
    public CommandParameter(String name, boolean optional) {
        this(name, CommandParamType.RAWTEXT, optional);
    }

    /**
     * @deprecated use {@link #newType(String, CommandParamType)} instead
     */
    @Deprecated
    public CommandParameter(String name) {
        this(name, false);
    }

    /**
     * @deprecated use {@link #newEnum(String, boolean, String)} instead
     */
    @Deprecated
    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(enumType, new HashSet<>());
    }

    /**
     * @deprecated use {@link #newEnum(String, boolean, String[])} instead
     */
    @Deprecated
    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(name + "Enums", enumValues);
    }

    /**
     * @deprecated use {@link #newEnum(String, String)} instead
     */
    @Deprecated
    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

    /**
     * @deprecated use {@link #newEnum(String, String[])} instead
     */
    @Deprecated
    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }

    private CommandParameter(String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        if (postFix != null && postFix.isBlank()) {
            throw new IllegalArgumentException("Postfix cannot be blank");
        }
        this.name = name;
        this.optional = optional;
        this.type = type;
        this.enumData = enumData;
        this.postFix = postFix;
    }

    public static CommandParameter newType(String name, CommandParamType type) {
        return newType(name, false, type);
    }

    public static CommandParameter newType(String name, boolean optional, CommandParamType type) {
        return new CommandParameter(name, optional, type, null, null);
    }

    public static CommandParameter newEnum(String name, String[] values) {
        return newEnum(name, false, values);
    }

    public static CommandParameter newEnum(String name, boolean optional, String[] values) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", values));
    }

    public static CommandParameter newEnum(String name, String type) {
        return newEnum(name, false, type);
    }

    public static CommandParameter newEnum(String name, boolean optional, String type) {
        return newEnum(name, optional, new CommandEnum(type, new HashSet<>()));
    }

    public static CommandParameter newEnum(String name, CommandEnum data) {
        return newEnum(name, false, data);
    }

    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data) {
        return new CommandParameter(name, optional, CommandParamType.RAWTEXT, data, null);
    }

    public static CommandParameter newPostfix(String name, String postfix) {
        return newPostfix(name, false, postfix);
    }

    public static CommandParameter newPostfix(String name, boolean optional, String postfix) {
        return new CommandParameter(name, optional, CommandParamType.RAWTEXT, null, postfix);
    }

    public CommandParameter addOption(CommandParamOption option) {
        this.options.add(option);
        return this;
    }

    protected static CommandParamType fromString(String param) {
        switch (param) {
            case "string":
            case "stringenum":
                return CommandParamType.STRING;
            case "target":
                return CommandParamType.TARGET;
            case "blockpos":
                return CommandParamType.POSITION;
            case "rawtext":
                return CommandParamType.RAWTEXT;
            case "int":
                return CommandParamType.INT;
        }

        return CommandParamType.RAWTEXT;
    }
}