package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.TextPacket;

public class DeveloperCommand extends VanillaCommand {

    public DeveloperCommand(String name) {
        super(name, "%nukkit.command.developer.description", "%nukkit.command.developer.usage");
        this.setPermission("nukkit.command.developer");
        this.commandParameters.clear();
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
            case "setblock":
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
                    block = Block.fromString(blockName);
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.setblock.notFound", blockName));
                    return false;
                }

                if (length > 7) {
                    try {
                        block = Block.get(block.getId(), Integer.parseInt(args[7]) & Block.BLOCK_META_MASK);
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
            case "text":
                TextPacket pk = new TextPacket();
                break;
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
}
