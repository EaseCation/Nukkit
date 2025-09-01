package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

public class SetBlockCommand extends VanillaCommand {
	public SetBlockCommand(String name) {
        super(name, "%commands.setblock.description", "%nukkit.command.setblock.usage");
        this.setPermission("nukkit.command.setblock");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
				CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
				CommandParameter.newEnum("tileName", CommandEnum.ENUM_BLOCK)
						.addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
				CommandParameter.newEnum("oldBlockHandling", true, new CommandEnum("SetBlockMode", SetBlockMode.values()))
						.addOption(CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION),
        });
        this.commandParameters.put("states", new CommandParameter[]{
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", CommandEnum.ENUM_BLOCK)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("blockStates", CommandParamType.BLOCK_STATES),
                CommandParameter.newEnum("oldBlockHandling", true, new CommandEnum("SetBlockMode", SetBlockMode.values()))
                        .addOption(CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION),
        });
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!this.testPermission(sender)) {
            return true;
        }

		CommandParser parser = new CommandParser(this, sender, args);
		try {
			Position position = parser.parsePosition();
			Block block = parser.parseBlock();
			SetBlockMode oldBlockHandling = parser.parseEnumOrDefault(SetBlockMode.REPLACE);

			Level level = position.getLevel();

			if (!level.getHeightRange().isValidBlockY(position.y)) {
				sender.sendMessage(TextFormat.RED + "Cannot place block outside of the world");
				return true;
			}

			boolean changed = false;
			switch (oldBlockHandling) {
				case DESTROY:
					level.useBreakOn(position);
				case REPLACE:
					changed = level.setExtraBlock(position, Blocks.air(), true, false);
					changed |= level.setBlock(position, block, true, false);
					break;
				case KEEP:
					if (level.getBlock(position).getId() == Block.AIR) {
						changed = level.setBlock(position, block, true, false);
					}
					break;
			}

			if (changed) {
				sender.sendMessage(new TranslationContainer("commands.setblock.success"));
				return true;
			} else {
				sender.sendMessage(TextFormat.RED + "The block couldn't be placed");
			}
		} catch (CommandSyntaxException e) {
			sender.sendMessage(parser.getErrorMessage());
		}
		return false;
	}

	enum SetBlockMode {
		REPLACE,
		DESTROY,
		KEEP,
	}
}
