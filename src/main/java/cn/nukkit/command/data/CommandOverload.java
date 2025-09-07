package cn.nukkit.command.data;

import lombok.ToString;

@ToString
public class CommandOverload {

    public CommandInput input = new CommandInput();
    public boolean chaining;

    public CommandOutput output = new CommandOutput();

}
