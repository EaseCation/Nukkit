package cn.nukkit.command.defaults;

import cn.nukkit.GameMode;
import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;

import java.util.List;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "%commands.gamemode.description", "%nukkit.command.gamemode.usage");
        this.setPermission("nukkit.command.gamemode.survival;" +
                "nukkit.command.gamemode.creative;" +
                "nukkit.command.gamemode.adventure;" +
                "nukkit.command.gamemode.spectator;" +
                "nukkit.command.gamemode.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("gameMode", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("byString", new CommandParameter[]{
                CommandParameter.newEnum("gameMode", CommandEnum.ENUM_GAMEMODE),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            GameMode gameMode = parser.parse(arg -> {
                GameMode type = GameMode.byIdentifier(arg);
                if (type != null) {
                    return type;
                }
                type = GameMode.byId(Integer.parseInt(arg));
                if (type == null) {
                    throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                }
                return type;
            });
            List<Player> targets = parser.parseTargetPlayersOrSelf();

            targets.forEach(target -> {
                boolean self = target == sender;
                if (!self && !sender.hasPermission("nukkit.command.gamemode.other")) {
                    return;
                }

                switch (gameMode) {
                    case SURVIVAL:
                        if (!sender.hasPermission("nukkit.command.gamemode.survival")) {
                            return;
                        }
                        break;
                    case CREATIVE:
                        if (!sender.hasPermission("nukkit.command.gamemode.creative")) {
                            return;
                        }
                        break;
                    case ADVENTURE:
                        if (!sender.hasPermission("nukkit.command.gamemode.adventure")) {
                            return;
                        }
                        break;
                    case SPECTATOR:
                        if (!sender.hasPermission("nukkit.command.gamemode.spectator")) {
                            return;
                        }
                        break;
                }

                target.setGamemode(gameMode.ordinal());

                target.sendMessage(new TranslationContainer("gameMode.changed", gameMode.getTranslationKey()));
                if (self) {
                    broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.self", gameMode.getTranslationKey()));
                } else {
                    broadcastCommandMessage(sender, new TranslationContainer("commands.gamemode.success.other", gameMode.getTranslationKey(), target.getName()));
                }
            });
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
