package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.TextFormat;
import com.google.common.annotations.Beta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Beta
public class CommandParser {

    private final Command command;
    private final CommandSender sender;
    private final String[] args;

    private int cursor = -1;
    private int marker = -1;
    private String errorMessage;

    public CommandParser(Command command, CommandSender sender, String... args) {
        this.command = command;
        this.sender = sender;
        this.args = args;
    }

    public void mark() {
        marker = cursor;
    }

    public void reset() {
        cursor = marker;
    }

    public boolean hasNext() {
        return this.hasNext(1);
    }

    public boolean hasNext(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number must be greater than 0");
        }
        return this.cursor + n < this.args.length;
    }

    private String next() throws CommandSyntaxException {
        if (++this.cursor >= this.args.length) {
            throw CommandExceptions.END_OF_COMMAND;
        }
        return this.args[this.cursor];
    }

    public void back() {
        this.back(1);
    }

    public void back(int n) {
        if (n <= 0) {
            return;
        }
        this.cursor -= n;
    }

    public void setErrorMessage(@Nullable String detailMessage) {
        this.errorMessage = detailMessage;
    }

    public String getErrorMessage() {
        if (errorMessage != null) {
            return TextFormat.RED + errorMessage;
        }

        StringBuilder left = new StringBuilder(10);
        String current;
        StringBuilder right = new StringBuilder(10);

        int limit = 10;
        for (int i = this.cursor - 1; i >= 0 && limit >= 0; i--) {
            left.insert(0, ' ');
            limit--;

            String arg = this.args[i];
            for (int j = arg.length() - 1; j >= 0 && limit-- >= 0; j--) {
                left.insert(0, arg.charAt(j));
            }
        }
        if (limit >= 0) {
            left.insert(0, ' ');
            limit--;

            String command = "/" + this.command.getName();
            for (int j = command.length() - 1; j >= 0 && limit-- >= 0; j--) {
                left.insert(0, command.charAt(j));
            }
        }

        if (this.cursor >= this.args.length) {
            current = "";
        } else {
            current = this.args[this.cursor];
            limit = 10;
            for (int i = this.cursor + 1; i < this.args.length && limit > 0; i++, limit--) {
                right.append(' ');
                limit--;

                String arg = this.args[i];
                for (int j = 0; j < arg.length() && limit-- > 0; j++) {
                    right.append(arg.charAt(j));
                }
            }
        }

        return String.format(TextFormat.RED + "Syntax error: Unexpected \"%2$s\": at \"%1$s>>%2$s<<%3$s\"", left, current, right);
    }

    public Level getTargetLevel() {
        Level level = null;

        if (this.sender instanceof Position) {
            level = ((Position) this.sender).getLevel();
        }

        return level == null ? this.sender.getServer().getDefaultLevel() : level;
    }

    public int parseInt() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_INT;
        }
    }

    public long parseLong() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_INT;
        }
    }

    public float parseFloat() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_FLOAT;
        }
    }

    public double parseDouble() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_FLOAT;
        }
    }

    public boolean parseBoolean() throws CommandSyntaxException {
        String arg = this.next();
        switch (arg.toLowerCase()) {
            case "true":
                return true;
            case "false":
                return false;
        }
        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
    }

    public String literal() throws CommandSyntaxException {
        return this.next();
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType) throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public List<Entity> parseTargets() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return EntitySelector.matchEntities(this.sender, arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public List<Player> parseTargetPlayers() throws CommandSyntaxException {
        return this.parseTargets().stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());
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
        String arg = this.next();
        try {
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

    public Block parseBlock() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Block.fromString(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public Item parseItem() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Item.fromString(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }
}
