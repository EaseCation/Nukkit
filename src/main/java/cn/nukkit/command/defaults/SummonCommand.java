package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.Mojangson;
import cn.nukkit.nbt.tag.CompoundTag;

public class SummonCommand extends Command {
	public SummonCommand(String name) {
        super(name, "%nukkit.command.summon.description", "%commands.summon.usage");
        this.setPermission("nukkit.command.summon");
        this.commandParameters.clear();
        this.commandParameters.put("pos", new CommandParameter[]{
                new CommandParameter("entityType", false, CommandParameter.ENUM_TYPE_ENTITY_LIST),
                new CommandParameter("spawnPos", CommandParameter.ARG_TYPE_BLOCK_POS, true),
        });
        this.commandParameters.put("nbt", new CommandParameter[]{
                new CommandParameter("entityType", false, CommandParameter.ENUM_TYPE_ENTITY_LIST),
                new CommandParameter("nbt", CommandParameter.ARG_TYPE_STRING, true)
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
        if (args.length == 1) {
        	String entityType = args[0];
        	Position position = player.getPosition();
			Entity entity = Entity.createEntity(entityType, position);
			if (entity == null) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return false;
			}
			entity.spawnToAll();
			sender.sendMessage(new TranslationContainer("commands.summon.success", entity.getSaveId()));
			return true;
		} else if (args.length == 2) {
        	String entityType = args[0];
			CompoundTag nbt;
			try {
				nbt = Mojangson.read(args[1]);
			} catch (IllegalArgumentException e) {
				sender.sendMessage(e.getMessage());
				return true;
			}
			Entity entity = Entity.createEntity(entityType, player.getChunk(), nbt);
			if (entity == null) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return false;
			}
			entity.spawnToAll();
			sender.sendMessage(new TranslationContainer("commands.summon.success", entity.getSaveId()));
			return true;
		} else if (args.length == 4) {
        	String entityType = args[0];
			Position position;
			try {
				double x = Double.parseDouble(args[1]);
				double y = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				position = new Position(x, y, z, player.getLevel());
			} catch (NumberFormatException e) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return false;
			}
			Entity entity = Entity.createEntity(entityType, position);
			if (entity == null) {
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
				return false;
			}
			entity.spawnToAll();
			sender.sendMessage(new TranslationContainer("commands.summon.success", entity.getSaveId()));
			return true;
		} else {
			sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
		}
	}
}