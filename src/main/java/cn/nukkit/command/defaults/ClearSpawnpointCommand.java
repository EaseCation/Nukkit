package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class ClearSpawnpointCommand extends VanillaCommand {
    public ClearSpawnpointCommand(String name) {
        super(name, "%commands.clearspawnpoint.description", "%nukkit.command.clearspawnpoint.usage");
        this.setPermission("nukkit.command.clearspawnpoint");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets = parser.parseTargetPlayersOrSelf();

            List<String> success = new ObjectArrayList<>(targets.size());

            targets.forEach(target -> {
                target.setSpawn(null);

                success.add(target.getName());
            });

            if (success.size() == 1) {
                broadcastCommandMessage(sender, new TranslationContainer("commands.clearspawnpoint.success.single", success.get(0)));
            } else {
                broadcastCommandMessage(sender, new TranslationContainer("commands.clearspawnpoint.success.multiple", String.join(", ", success)));
            }
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
