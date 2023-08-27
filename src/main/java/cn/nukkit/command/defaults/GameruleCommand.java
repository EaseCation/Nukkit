package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandFlag;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class GameruleCommand extends VanillaCommand {

    public GameruleCommand(String name) {
        super(name, "%commands.gamerule.description", "%nukkit.command.gamerule.usage");
        this.setPermission("nukkit.command.gamerule");
        this.commandData.flags.add(CommandFlag.NOT_CHEAT);
        this.commandParameters.clear();

        GameRules rules = GameRules.getDefault();
        Set<String> boolGameRules = new ObjectOpenHashSet<>();
        Set<String> intGameRules = new ObjectOpenHashSet<>();
        Set<String> floatGameRules = new ObjectOpenHashSet<>();
        Set<String> unknownGameRules = new ObjectOpenHashSet<>();

        rules.getGameRulesUnsafe().forEach((rule, value) -> {
            switch (value.getType()) {
                case BOOLEAN:
                    boolGameRules.add(rule.getBedrockName());
                    break;
                case INTEGER:
                    intGameRules.add(rule.getBedrockName());
                    break;
                case FLOAT:
                    floatGameRules.add(rule.getBedrockName());
                    break;
                case UNKNOWN:
                default:
                    unknownGameRules.add(rule.getBedrockName());
                    break;
            }
        });

        if (!boolGameRules.isEmpty()) {
            this.commandParameters.put("boolGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("BoolGameRule", boolGameRules)),
                    CommandParameter.newEnum("value", true, CommandEnum.ENUM_BOOLEAN)
            });
        }
        if (!intGameRules.isEmpty()) {
            this.commandParameters.put("intGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("IntGameRule", intGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.INT)
            });
        }
        if (!floatGameRules.isEmpty()) {
            this.commandParameters.put("floatGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("FloatGameRule", floatGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.FLOAT)
            });
        }
        if (!unknownGameRules.isEmpty()) {
            this.commandParameters.put("unknownGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("UnknownGameRule", unknownGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.STRING)
            });
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (!sender.isPlayer()) {
            sender.sendMessage(new TranslationContainer("nukkit.command.generic.ingame"));
            return true;
        }
        GameRules rules = ((Player) sender).getLevel().getGameRules();

        switch (args.length) {
            case 0:
                sender.sendMessage(rules.toString());
                return true;
            case 1:
                Optional<GameRule> gameRule = GameRule.parseString(args[0]);
                if (gameRule.isEmpty() || !rules.hasRule(gameRule.get())) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule", args[0]));
                    return true;
                }

                sender.sendMessage(gameRule.get().getBedrockName() + " = " + rules.getString(gameRule.get()));
                return true;
            default:
                Optional<GameRule> optionalRule = GameRule.parseString(args[0]);

                if (optionalRule.isEmpty()) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax",
                            "/gamerule ", args[0], " " + String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    return true;
                }

                try {
                    rules.setGameRules(optionalRule.get(), args[1]);
                    sender.sendMessage(new TranslationContainer("commands.gamerule.success", optionalRule.get().getBedrockName(), args[1]));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule "  + args[0] + " ", args[1], " " + String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
                }
                return true;
        }
    }
}
