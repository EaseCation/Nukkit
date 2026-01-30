package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;

import java.util.List;

/**
 * PlayAnimation command - Sends animation requests to clients to play entity animations
 * @since 1.16.100
 */
public class PlayAnimationCommand extends VanillaCommand {
    public PlayAnimationCommand(String name) {
        super(name, "%commands.playanimation.description", "%nukkit.command.playanimation.usage");
        this.setPermission("nukkit.command.playanimation");
        this.commandParameters.clear();

        // Basic usage: /playanimation <entity> <animation>
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING)
        });

        // With next_state parameter
        this.commandParameters.put("nextState", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING),
                CommandParameter.newType("nextState", true, CommandParamType.STRING)
        });

        // With blend_out_time parameter
        this.commandParameters.put("blendOutTime", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING),
                CommandParameter.newType("nextState", true, CommandParamType.STRING),
                CommandParameter.newType("blendOutTime", true, CommandParamType.FLOAT)
        });

        // With stop_expression parameter
        this.commandParameters.put("stopExpression", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING),
                CommandParameter.newType("nextState", true, CommandParamType.STRING),
                CommandParameter.newType("blendOutTime", true, CommandParamType.FLOAT),
                CommandParameter.newType("stopExpression", true, CommandParamType.STRING)
        });

        // Full parameters: with controller parameter
        this.commandParameters.put("full", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING),
                CommandParameter.newType("nextState", true, CommandParamType.STRING),
                CommandParameter.newType("blendOutTime", true, CommandParamType.FLOAT),
                CommandParameter.newType("stopExpression", true, CommandParamType.STRING),
                CommandParameter.newType("controller", true, CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            // Parse target entities
            List<Entity> targets = parser.parseTargets();

            // Parse animation name (required)
            String animation = parser.literal();

            // Parse optional parameters
            String nextState = parser.literalOrDefault("default");
            float blendOutTime = parser.parseFloatOrDefault(0.0f);
            String stopExpression = parser.literalOrDefault("");
            String controller = parser.literalOrDefault("__runtime_controller");

            if (targets.isEmpty()) {
                sender.sendMessage(new TranslationContainer("commands.generic.noTargetMatch"));
                return false;
            }

            // Collect all target entity runtime IDs
            long[] entityRuntimeIds = targets.stream()
                    .mapToLong(Entity::getId)
                    .toArray();

            // Send animation to all target entities and their viewers
            for (Entity target : targets) {
                // Send to target entity itself (if it's a player)
                if (target instanceof Player player) {
                    player.playAnimation(animation, nextState, stopExpression, controller, blendOutTime, entityRuntimeIds);
                }

                // Send to all viewers
                for (Player viewer : target.getViewers().values()) {
                    viewer.playAnimation(animation, nextState, stopExpression, controller, blendOutTime, entityRuntimeIds);
                }
            }

            // Send success message
            broadcastCommandMessage(sender, new TranslationContainer("commands.playanimation.success"));

            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.exception"));
        }
        return false;
    }
}