package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandFlag;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.TextPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DeveloperCommand extends Command {

    public DeveloperCommand(String name) {
        super(name, "%nukkit.command.developer.description", "%nukkit.command.developer.usage");
        this.setPermission("nukkit.command.developer");
        this.commandData.flags.add(CommandFlag.TEST);
        this.commandParameters.clear();
        this.commandParameters.put("varI64", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandVar", "var")),
                CommandParameter.newEnum("valueType", new CommandEnum("VarActionSetInt", "i")),
                CommandParameter.newType("name", CommandParamType.STRING),
                CommandParameter.newType("data", true, CommandParamType.INT),
        });
        this.commandParameters.put("varF32", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandVar", "var")),
                CommandParameter.newEnum("valueType", new CommandEnum("VarActionSetFloat", "f")),
                CommandParameter.newType("name", CommandParamType.STRING),
                CommandParameter.newType("data", true, CommandParamType.FLOAT),
        });
        this.commandParameters.put("varStr", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandVar", "var")),
                CommandParameter.newEnum("valueType", new CommandEnum("VarActionSetString", "s")),
                CommandParameter.newType("name", CommandParamType.STRING),
                CommandParameter.newType("data", true, CommandParamType.STRING),
        });
        this.commandParameters.put("varDel", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandVar", "var")),
                CommandParameter.newEnum("action", new CommandEnum("VarActionRemove", "r")),
                CommandParameter.newType("name", CommandParamType.STRING),
        });
        this.commandParameters.put("setblock", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandSetblock", "setblock")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("layer", CommandParamType.INT),
                CommandParameter.newEnum("update", CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("block", CommandEnum.ENUM_BLOCK)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("meta", true, CommandParamType.INT),
        });
        this.commandParameters.put("text", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandText", "text")),
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("message", CommandParamType.STRING),
                CommandParameter.newType("source", true, CommandParamType.STRING),
        });
        this.commandParameters.put("heightmap", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandHeightmap", "heightmap")),
                CommandParameter.newType("position", true, CommandParamType.POSITION),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        int length = args.length;
        if (length == 0) {
            sendUsage(sender);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "var": {
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.literal();
                    Function<String, Object> valueParser = parser.parse(input -> {
                        switch (input) {
                            case "i":
                                return Long::valueOf;
                            case "f":
                                return Float::valueOf;
                            case "s":
                                return String::valueOf;
                            case "r":
                                return null;
                            default:
                                throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                        }
                    });
                    String key = parser.literal();
                    Object data = valueParser != null ? parser.parse(valueParser) : null;

                    if (data != null) {
                        DataHolder.VARIABLES.put(key, data);
                        sender.sendMessage(key + " = " + data);
                    } else {
                        if (DataHolder.VARIABLES.remove(key) != null) {
                            sender.sendMessage("succ: " + key);
                        } else {
                            sender.sendMessage("fail: " + key);
                        }
                    }
                    return true;
                } catch (CommandSyntaxException e) {
                    sender.sendMessage(parser.getErrorMessage());
                }
                return false;
            }
            case "setblock": {
                if (length < 7) {
                    sendUsage(sender);
                    return false;
                }

                Position pos = new Position(sender instanceof Position ? (Position) sender : null);
                int layer;

                try {
                    pos.x = parseCoordinate(args[1], pos.x);
                    pos.y = parseCoordinate(args[2], pos.y);
                    pos.z = parseCoordinate(args[3], pos.z);

                    layer = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sendUsage(sender);
                    return false;
                }

                boolean update;
                try {
                    update = parseBoolean(args[5]);
                } catch (IllegalArgumentException e) {
                    sendUsage(sender);
                    return false;
                }

                Block block;
                String blockName = args[6];
                try {
                    block = Block.fromString(blockName, true);
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.setblock.notFound", blockName));
                    return false;
                }

                if (length > 7) {
                    try {
                        block = Block.get(block.getId(), Integer.parseInt(args[7]));
                    } catch (Exception e) {
                        sendUsage(sender);
                        return false;
                    }
                }

                if (pos.level == null) {
                    pos.level = sender.getServer().getDefaultLevel();
                }

                if (!pos.level.isValidHeight(pos.getFloorY())) {
                    sender.sendMessage(new TranslationContainer("commands.setblock.outOfWorld"));
                    return false;
                }

                if (!pos.level.setBlock(layer, pos, block, true, update)) {
                    sender.sendMessage(new TranslationContainer("commands.setblock.noChange"));
                    return false;
                }

                sender.sendMessage(new TranslationContainer("commands.setblock.success"));
                sender.sendMessage(block.toString());
                return true;
            }
            case "text": {
                TextPacket pk = new TextPacket();
                break;
            }
            case "heightmap": {
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.literal();
                    Position pos = parser.parsePositionOrDefault(() -> sender instanceof Position ? (Position) sender : new Position(0, 0, 0, sender.getServer().getDefaultLevel()));

                    int x = pos.getFloorX();
                    int z = pos.getFloorZ();
                    int height = pos.getLevel().getHeightMap(x, z);

                    sender.sendMessage("heightmap: " + x + ", " + z + " = " + height + " (" + (height >> 4) + " | " + (height & 0xf) +  ")");
                    return true;
                } catch (CommandSyntaxException e) {
                    sender.sendMessage(parser.getErrorMessage());
                }
                return false;
            }
        }

        sendUsage(sender);
        return false;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
    }

    private static boolean parseBoolean(String bool) throws IllegalArgumentException {
        switch (bool.toLowerCase()) {
            case "true":
                return true;
            case "false":
                return false;
        }
        throw new IllegalArgumentException("Invalid Boolean");
    }

    private static double parseCoordinate(String coord, double base) throws NumberFormatException {
        if (coord.startsWith("~")) {
            base += Double.parseDouble(coord.substring(1));
        } else {
            base = Double.parseDouble(coord);
        }
        return base;
    }

    public static class DataHolder {
        public static final Map<String, Object> VARIABLES = new HashMap<>();

        public static long getAsLong(String key) {
            return getAsLong(key, 0);
        }

        public static long getAsLong(String key, long defaultValue) {
            Objects.requireNonNull(key);
            return Optional.ofNullable(VARIABLES.get(key))
                    .filter(data -> data instanceof Number)
                    .map(data -> ((Number) data).longValue())
                    .orElse(defaultValue);
        }

        public static float getAsFloat(String key) {
            return getAsFloat(key, 0);
        }

        public static float getAsFloat(String key, float defaultValue) {
            Objects.requireNonNull(key);
            return Optional.ofNullable(VARIABLES.get(key))
                    .filter(data -> data instanceof Number)
                    .map(data -> ((Number) data).floatValue())
                    .orElse(defaultValue);
        }

        public static String getAsString(String key) {
            return getAsString(key, "");
        }

        public static String getAsString(String key, String defaultValue) {
            Objects.requireNonNull(key);
            return Optional.ofNullable(VARIABLES.get(key))
                    .map(String::valueOf)
                    .orElse(defaultValue);
        }
    }
}
