package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.math.Vector3;

public class SetBlockCommand extends Command {
	public SetBlockCommand(String name) {
        super(name, "%nukkit.command.setblock.description", "%commands.setblock.usage");
        this.setPermission("nukkit.command.setblock");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("position", CommandParameter.ARG_TYPE_BLOCK_POS, true),
                new CommandParameter("block", CommandParameter.ARG_TYPE_STRING, true)
        });
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!this.testPermission(sender)) {
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
		}

		Player player = (Player) sender;
		if (args.length == 4) {
			Vector3 position;
			try {
				double x = Double.parseDouble(args[0]);
				double y = Double.parseDouble(args[1]);
				double z = Double.parseDouble(args[2]);
				position = new Vector3(x, y, z);
			} catch (NumberFormatException e) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return false;
			}
			Block block;
			try {
				block = Block.fromString(args[3]);
			} catch (Exception e) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return true;
			}
			if (!player.getLevel().setBlock(position, block)) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return true;
			}
			sender.sendMessage(new TranslationContainer("commands.setblock.success"));
			return true;
		} else {
			sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
		}
	}
}
