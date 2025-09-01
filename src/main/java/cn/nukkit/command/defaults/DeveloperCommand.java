package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
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
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.TextPacket;

import java.util.*;
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
        this.commandParameters.put("setblockStates", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandSetblock", "setblock")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("layer", CommandParamType.INT),
                CommandParameter.newEnum("update", CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("block", CommandEnum.ENUM_BLOCK)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("blockStates", CommandParamType.BLOCK_STATES),
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
        this.commandParameters.put("levelEv", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandLevelEvent", "levelev")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("event", CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
        });
        this.commandParameters.put("soundEv", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandSoundEvent", "soundev")),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("event", CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newEnum("entity", true, CommandEnum.ENUM_ENTITY_TYPE),
                CommandParameter.newEnum("baby", true, CommandEnum.ENUM_BOOLEAN),
                CommandParameter.newEnum("global", true, CommandEnum.ENUM_BOOLEAN),
        });
        this.commandParameters.put("actorEv", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandActorEvent", "actorev")),
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("event", CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
        });
        this.commandParameters.put("damage", new CommandParameter[]{
                CommandParameter.newEnum("subCommand", new CommandEnum("SubCommandDamage", "damage")),
                CommandParameter.newType("entity", true, CommandParamType.TARGET),
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
                    block = Block.fromStringNullable(blockName, true);
                    if (block == null) {
                        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                    }
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

                if (!pos.level.getHeightRange().isValidBlockY(pos.getFloorY())) {
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
                    int rawValue = height + pos.getLevel().getHeightRange().getYIndexOffset();

                    sender.sendMessage("heightmap: " + x + ", " + z + " = " + height + " (" + (height >> 4) + " | " + (height & 0xf) +  ") " + rawValue);
                    return true;
                } catch (CommandSyntaxException e) {
                    sender.sendMessage(parser.getErrorMessage());
                }
                return false;
            }
            case "levelev": {
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.literal();
                    Position pos = parser.parsePositionOrDefault(() -> sender instanceof Position ? (Position) sender : new Position(0, 0, 0, sender.getServer().getDefaultLevel()));
                    int event = parser.parseInt();
                    int data = parser.parseIntOrDefault(0);

                    LevelEventPacket packet = new LevelEventPacket();
                    packet.x = (float) pos.x;
                    packet.y = (float) pos.y;
                    packet.z = (float) pos.z;
                    packet.evid = event;
                    packet.data = data;
                    if (sender instanceof Player player) {
                        player.dataPacket(packet);
                    } else {
                        pos.level.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
                    }
                    sender.sendMessage(packet.toString());
                    return true;
                } catch (CommandSyntaxException e) {
                    sender.sendMessage(parser.getErrorMessage());
                }
                return false;
            }
            case "soundev": {
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.literal();
                    Position pos = parser.parsePositionOrDefault(() -> sender instanceof Position ? (Position) sender : new Position(0, 0, 0, sender.getServer().getDefaultLevel()));
                    int event = parser.parseInt();
                    int data = parser.parseIntOrDefault(0);
                    String entityType = parser.literalOrDefault(":");
                    boolean baby = parser.parseBooleanOrDefault(false);
                    boolean global = parser.parseBooleanOrDefault(false);
                    List<Entity> target = parser.parseTargetsOrDefault(1, Collections.emptyList());

                    LevelSoundEventPacket packet = new LevelSoundEventPacket();
                    packet.x = (float) pos.x;
                    packet.y = (float) pos.y;
                    packet.z = (float) pos.z;
                    packet.sound = event;
                    packet.extraData = data;
                    packet.entityIdentifier = entityType;
                    packet.isBabyMob = baby;
                    packet.isGlobal = global;
                    if (!target.isEmpty()) {
                        packet.entityUniqueId = target.get(0).getId();
                    }
                    if (sender instanceof Player player) {
                        player.dataPacket(packet);
                    } else {
                        pos.level.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
                    }
                    sender.sendMessage(packet.toString());
                    return true;
                } catch (CommandSyntaxException e) {
                    sender.sendMessage(parser.getErrorMessage());
                }
                return false;
            }
            case "actorev": {
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.literal();
                    List<Entity> entities = parser.parseTargets();
                    int event = parser.parseInt();
                    int data = parser.parseIntOrDefault(0);

                    for (Entity entity : entities) {
                        EntityEventPacket packet = new EntityEventPacket();
                        packet.eid = entity.getId();
                        packet.event = event;
                        packet.data = data;
                        if (sender instanceof Player player) {
                            player.dataPacket(packet);
                        } else {
                            Server.broadcastPacket(entity.getViewers().values(), packet);
                        }
                    }
                    sender.sendMessage("success");
                    return true;
                } catch (CommandSyntaxException e) {
                    sender.sendMessage(parser.getErrorMessage());
                }
                return false;
            }
            case "damage": {
                CommandParser parser = new CommandParser(this, sender, args);
                try {
                    parser.literal();
                    List<Entity> entities = parser.parseTargetsOrSelf();

                    for (Entity entity : entities) {
                        EntityDamageByEntityEvent event0 = new EntityDamageByEntityEvent(entity, entity, DamageCause.PROJECTILE, 0);
                        if (entity.attack(event0)) {
                            sender.sendMessage("projectile 0 true : set -0 health, actual -0 health");
                        } else {
                            sender.sendMessage("projectile 0 false: set -0 health, actual -0 health");
                        }

                        EntityDamageByEntityEvent event1 = new EntityDamageByEntityEvent(entity, entity, DamageCause.ENTITY_ATTACK, 1);
                        if (entity.attack(event1)) {
                            sender.sendMessage("melee 1 true : set -1 health, actual -1 health (diff)");
                        } else {
                            sender.sendMessage("melee 1 false: set -1 health, actual -1 health (diff)");
                        }

                        EntityDamageByEntityEvent event2 = new EntityDamageByEntityEvent(entity, entity, DamageCause.ENTITY_ATTACK, 1);
                        if (entity.attack(event2)) {
                            sender.sendMessage("melee 2 true : set -1 health, actual -0 health (CD)");
                        } else {
                            sender.sendMessage("melee 2 false: set -1 health, actual -0 health (CD)");
                        }

                        EntityDamageByEntityEvent event3 = new EntityDamageByEntityEvent(entity, entity, DamageCause.ENTITY_ATTACK, 2);
                        if (entity.attack(event3)) {
                            sender.sendMessage("melee 3 true : set -2 health, actual -1 health (diff)");
                        } else {
                            sender.sendMessage("melee 3 false: set -2 health, actual -1 health (diff)");
                        }

                        EntityDamageByEntityEvent event4 = new EntityDamageByEntityEvent(entity, entity, DamageCause.ENTITY_ATTACK, 2);
                        if (entity.attack(event4)) {
                            sender.sendMessage("melee 4 true : set -2 health, actual -0 health (CD)");
                        } else {
                            sender.sendMessage("melee 4 false: set -2 health, actual -0 health (CD)");
                        }

                        EntityDamageByEntityEvent event5 = new EntityDamageByEntityEvent(entity, entity, DamageCause.ENTITY_ATTACK, 5);
                        if (entity.attack(event5)) {
                            sender.sendMessage("melee 5 true : set -5 health, actual -3 health (diff)");
                        } else {
                            sender.sendMessage("melee 5 false: set -5 health, actual -3 health (diff)");
                        }
                    }

                    sender.sendMessage("success");
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
