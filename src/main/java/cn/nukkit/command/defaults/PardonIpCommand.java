package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandFlag;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PardonIpCommand extends VanillaCommand {

    public PardonIpCommand(String name) {
        super(name, "%nukkit.command.unban.ip.description", "%nukkit.command.unbanip.usage", "unbanip");
        this.setPermission("nukkit.command.unban.ip");
        this.commandData.flags.add(CommandFlag.NOT_CHEAT);
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("ip", CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String value = args[0];

        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", value)) {
            sender.getServer().getIPBans().remove(value);

            try {
                sender.getServer().getNetwork().unblockAddress(InetAddress.getByName(value));
            } catch (UnknownHostException e) {
                sender.sendMessage(new TranslationContainer("commands.unbanip.invalid"));
                return true;
            }

            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.unbanip.success", value));
        } else {

            sender.sendMessage(new TranslationContainer("commands.unbanip.invalid"));
        }

        return true;
    }
}
