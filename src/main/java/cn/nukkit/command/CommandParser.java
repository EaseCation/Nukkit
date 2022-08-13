package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CommandParser {

    private final Command command;
    private final CommandSender sender;
    private final String[] args;

    private int cursor = -1;

    public CommandParser(Command command, CommandSender sender, String[] args) {
        this.command = command;
        this.sender = sender;
        this.args = args;
    }

    private String next() throws CommandSyntaxException {
        if (++this.cursor >= this.args.length) {
            throw CommandExceptions.END_OF_COMMAND;
        }
        return this.args[this.cursor];
    }

    public String getErrorMessage() {
        String parameter1;
        try {
            StringJoiner joiner = new StringJoiner(" ", " ", " ");
            for (String arg : Arrays.copyOfRange(this.args, 0, this.cursor)) {
                joiner.add(arg);
            }
            parameter1 = joiner.length() < 3 ? "" : joiner.toString();
        } catch (Exception e) {
            parameter1 = "";
        }

        String parameter2;
        try {
            parameter2 = this.args[this.cursor];
        } catch (Exception e) {
            parameter2 = "";
        }

        String parameter3;
        try {
            StringJoiner joiner = new StringJoiner(" ", " ", "");
            for (String arg : Arrays.copyOfRange(this.args, this.cursor + 1, this.args.length)) {
                joiner.add(arg);
            }
            parameter3 = joiner.toString();
        } catch (Exception e) {
            parameter3 = "";
        }

        return String.format(TextFormat.RED + "Syntax error: Unexpected \"%2$s\": at \"/%4$s%1$s>>%2$s<<%3$s\"", parameter1, parameter2, parameter3, this.command.getName());
    }

    public Level getTargetLevel() {
        Level level = null;

        if (this.sender instanceof Position) {
            level = ((Position) this.sender).getLevel();
        }

        return level == null ? this.sender.getServer().getDefaultLevel() : level;
    }

    public int parseInt() throws CommandSyntaxException {
        try {
            String arg = this.next();
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public double parseDouble() throws CommandSyntaxException {
        try {
            String arg = this.next();
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public boolean parseBoolean() throws CommandSyntaxException {
        try {
            String arg = this.next();
            switch (arg.toLowerCase()) {
                case "true":
                    return true;
                case "false":
                    return false;
            }
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
    }

    public String parseString() throws CommandSyntaxException {
        try {
            return this.next();
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType) throws CommandSyntaxException {
        try {
            String arg = this.next();
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public List<Entity> parseTargets() throws CommandSyntaxException {
        try {
            String arg = this.next();
            return EntitySelector.matchEntities(this.sender, arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public List<Player> parseTargetPlayers() throws CommandSyntaxException {
        try {
            return this.parseTargets().stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public Position parsePosition() throws CommandSyntaxException {
        return Position.fromObject(this.parseVector3(), this.getTargetLevel());
    }

    public Vector3 parseVector3() throws CommandSyntaxException {
        double baseX = 0;
        double baseY = 0;
        double baseZ = 0;

        if (this.sender instanceof Vector3) {
            Vector3 vec = (Vector3) this.sender;
            baseX = vec.getX();
            baseY = vec.getY();
            baseZ = vec.getZ();
        } else if (this.sender instanceof Vector3f) {
            Vector3f vec = (Vector3f) this.sender;
            baseX = vec.getX();
            baseY = vec.getY();
            baseZ = vec.getZ();
        } else if (this.sender instanceof BlockVector3) {
            BlockVector3 vec = (BlockVector3) this.sender;
            baseX = vec.getX();
            baseY = vec.getY();
            baseZ = vec.getZ();
        }

        return new Vector3(this.parseCoordinate(baseX), this.parseCoordinate(baseY), this.parseCoordinate(baseZ));
    }

    public Vector2 parseVector2() throws CommandSyntaxException {
        double baseX = 0;
        double baseZ = 0;

        if (this.sender instanceof Vector3) {
            Vector3 vec = (Vector3) this.sender;
            baseX = vec.getX();
            baseZ = vec.getZ();
        } else if (this.sender instanceof Vector3f) {
            Vector3f vec = (Vector3f) this.sender;
            baseX = vec.getX();
            baseZ = vec.getZ();
        } else if (this.sender instanceof BlockVector3) {
            BlockVector3 vec = (BlockVector3) this.sender;
            baseX = vec.getX();
            baseZ = vec.getZ();
        } else if (this.sender instanceof Vector2) {
            Vector2 vec = (Vector2) this.sender;
            baseX = vec.getX();
            baseZ = vec.getY();
        }

        return new Vector2(this.parseCoordinate(baseX), this.parseCoordinate(baseZ));
    }

    private double parseCoordinate(double baseCoordinate) throws CommandSyntaxException {
        try {
            String arg = this.next();
            if (arg.startsWith("~")) {
                String relativeCoordinate = arg.substring(1);
                if (relativeCoordinate.isEmpty()) {
                    return baseCoordinate;
                }
                return baseCoordinate + Double.parseDouble(relativeCoordinate);
            }
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }
}
