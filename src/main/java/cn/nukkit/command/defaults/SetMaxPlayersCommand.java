package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;

public class SetMaxPlayersCommand extends VanillaCommand {

    public SetMaxPlayersCommand(String name) {
        super(name, "%nukkit.command.setmaxplayers.description", "%nukkit.command.setmaxplayers.usage");
        this.setPermission("nukkit.command.setmaxplayers");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("maxPlayers", CommandParamType.INT),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            int maxPlayers = parser.parseInt();
            boolean lowerBound = false;

            if (maxPlayers < 1) {
                maxPlayers = 1;
                lowerBound = true;
            }

            sender.getServer().setMaxPlayers(maxPlayers);

            broadcastCommandMessage(sender, new TranslationContainer("%commands.setmaxplayers.success", maxPlayers));

            if (lowerBound) {
                sender.sendMessage("(Bound to minimum allowed connections)");
            }
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
