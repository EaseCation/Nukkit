package cn.nukkit.network.protocol;

import cn.nukkit.command.data.*;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AvailableCommandsPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.AVAILABLE_COMMANDS_PACKET;
    public Map<String, CommandDataVersions> commands;
    public final List<CommandEnum> softEnums = new ArrayList<>();

    public static final int ARG_FLAG_VALID = 0x100000;
    public static final int ARG_TYPE_INT = 0x01;
    public static final int ARG_TYPE_FLOAT = 0x02;
    public static final int ARG_TYPE_VALUE = 0x03;
    public static final int ARG_TYPE_TARGET = 0x04;

    public static final int ARG_TYPE_STRING = 0x0d;
    public static final int ARG_TYPE_POSITION = 0x0e;

    public static final int ARG_TYPE_RAWTEXT = 0x11;
    public static final int ARG_TYPE_TEXT = 0x13;

    public static final int ARG_TYPE_JSON = 0x16;
    public static final int ARG_TYPE_COMMAND = 0x1d;

    public static final int ARG_TYPE_BLOCK_STATES = 67;
    public static final int ARG_TYPE_BLOCK_POSITION = 47;
    public static final int ARG_TYPE_EQUIPMENT_SLOT = 38;

    public static final int ARG_FLAG_ENUM = 0x200000;
    public static final int ARG_FLAG_POSTFIX = 0x1000000;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();

        LinkedHashSet<String> enumValues = new LinkedHashSet<>();
        LinkedHashSet<String> postFixes = new LinkedHashSet<>();
        LinkedHashSet<CommandEnum> enums = new LinkedHashSet<>();

        commands.forEach((name, data) -> {
            CommandData cmdData = data.versions.get(0);

            if (cmdData.aliases != null) {
                enums.add(new CommandEnum(cmdData.aliases.getName(), cmdData.aliases.getValues()));

                enumValues.addAll(cmdData.aliases.getValues());
            }

            for (CommandOverload overload : cmdData.overloads.values()) {
                for (CommandParameter parameter : overload.input.parameters) {
                    if (parameter.enumData != null) {
                        enums.add(new CommandEnum(parameter.enumData.getName(), parameter.enumData.getValues()));

                        enumValues.addAll(parameter.enumData.getValues());
                    }

                    if (parameter.postFix != null) {
                        postFixes.add(parameter.postFix);
                    }
                }
            }
        });

        List<String> enumIndexes = new ArrayList<>(enumValues);
        List<String> enumDataIndexes = enums.stream().map(CommandEnum::getName).collect(Collectors.toList());
        List<String> fixesIndexes = new ArrayList<>(postFixes);

        this.putUnsignedVarInt(enumValues.size());
        for (String enumValue : enumValues) {
            putString(enumValue);
        }

        this.putUnsignedVarInt(postFixes.size());
        for (String postFix : postFixes) {
            putString(postFix);
        }

        this.putUnsignedVarInt(enums.size());
        enums.forEach((cmdEnum) -> {
            putString(cmdEnum.getName());

            List<String> values = cmdEnum.getValues();
            putUnsignedVarInt(values.size());

            for (String val : values) {
                int i = enumIndexes.indexOf(val);

                if (i < 0) {
                    throw new IllegalStateException("Enum value '" + val + "' not found");
                }

                if (enums.size() < 256) {
                    putByte((byte) i);
                } else if (enums.size() < 65536) {
                    putShort(i);
                } else {
                    putLInt(i);
                }
            }
        });

        putUnsignedVarInt(commands.size());

        commands.forEach((name, cmdData) -> {
            CommandData data = cmdData.versions.get(0);

            putString(name);
            putString(data.description);
            putByte((byte) data.flags);
            putByte((byte) data.permission);

            putLInt(data.aliases == null ? -1 : enumDataIndexes.indexOf(data.aliases.getName()));

            putUnsignedVarInt(data.overloads.size());
            for (CommandOverload overload : data.overloads.values()) {
                putUnsignedVarInt(overload.input.parameters.length);

                for (CommandParameter parameter : overload.input.parameters) {
                    putString(parameter.name);

                    int type;
                    if (parameter.enumData != null) {
                        type = ARG_FLAG_ENUM | ARG_FLAG_VALID | enumDataIndexes.indexOf(parameter.enumData.getName());
                    } else if (parameter.postFix != null) {
                        int i = fixesIndexes.indexOf(parameter.postFix);
                        if (i < 0) {
                            throw new IllegalStateException("Postfix '" + parameter.postFix + "' isn't in postfix array");
                        }


                        type = (ARG_FLAG_VALID | parameter.type.getId()) << 24 | i;
                    } else {
                        type = parameter.type.getId() | ARG_FLAG_VALID;
                    }

                    putLInt(type);
                    putBoolean(parameter.optional);
                }
            }
        });
    }
}
