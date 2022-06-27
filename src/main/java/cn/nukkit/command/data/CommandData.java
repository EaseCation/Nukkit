package cn.nukkit.command.data;

import lombok.ToString;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ToString
public class CommandData implements Cloneable {

    public CommandEnum aliases = null;
    public String description = "description";
    public Map<String, CommandOverload> overloads = new HashMap<>();

    public Set<CommandFlag> flags = EnumSet.noneOf(CommandFlag.class);
    public CommandPermission permission = CommandPermission.ALL;

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }
}
