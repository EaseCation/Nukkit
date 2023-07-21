package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.function.FloatSupplier;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class CommandParser {

    private final Command command;
    private final CommandSender sender;
    private final String[] args;

    private int cursor = -1;
    private int marker = -1;
    private TextContainer errorMessage;

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

    public void clearErrorMessage() {
        this.errorMessage = null;
    }

    public void setErrorMessage(@Nullable TextContainer detailMessage) {
        this.errorMessage = detailMessage;

        if (detailMessage != null) {
            detailMessage.setText(TextFormat.RED + detailMessage.getText());
        }
    }

    public TextContainer getErrorMessage() {
        if (errorMessage != null) {
            return errorMessage;
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

        return new TranslationContainer(TextFormat.RED + "%commands.generic.syntax", left.toString(), current, right.toString());
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

    public int parseInt(int min) throws CommandSyntaxException {
        return parseInt(min, Integer.MAX_VALUE);
    }

    public int parseInt(int min, int max) throws CommandSyntaxException {
        int num = this.parseInt();

        if (num < min) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.num.tooSmall", num, min));
            throw CommandExceptions.NUMBER_TOO_SMALL;
        }
        if (num > max) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.num.tooBig", num, max));
            throw CommandExceptions.NUMBER_TOO_BIG;
        }

        return num;
    }

    public int parseIntOrDefault(int defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseInt();
    }

    public int parseIntOrDefault(int defaultValue, int min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseInt(min);
    }

    public int parseIntOrDefault(int defaultValue, int min, int max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseInt(min, max);
    }

    public int parseIntOrDefault(IntSupplier defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsInt();
        }
        return this.parseInt();
    }

    public int parseIntOrDefault(IntSupplier defaultValue, int min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsInt();
        }
        return this.parseInt(min);
    }

    public int parseIntOrDefault(IntSupplier defaultValue, int min, int max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsInt();
        }
        return this.parseInt(min, max);
    }

    public long parseLong() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_INT;
        }
    }

    public long parseLong(long min) throws CommandSyntaxException {
        return parseLong(min, Long.MAX_VALUE);
    }

    public long parseLong(long min, long max) throws CommandSyntaxException {
        long num = this.parseLong();

        if (num < min) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.num.tooSmall", num, min));
            throw CommandExceptions.NUMBER_TOO_SMALL;
        }
        if (num > max) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.num.tooBig", num, max));
            throw CommandExceptions.NUMBER_TOO_BIG;
        }

        return num;
    }

    public long parseLongOrDefault(long defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseLong();
    }

    public long parseLongOrDefault(long defaultValue, long min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseLong(min);
    }

    public long parseLongOrDefault(long defaultValue, long min, long max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseLong(min, max);
    }

    public long parseLongOrDefault(LongSupplier defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsLong();
        }
        return this.parseLong();
    }

    public long parseLongOrDefault(LongSupplier defaultValue, long min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsLong();
        }
        return this.parseLong(min);
    }

    public long parseLongOrDefault(LongSupplier defaultValue, long min, long max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsLong();
        }
        return this.parseLong(min, max);
    }

    public float parseFloat() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_FLOAT;
        }
    }

    public float parseFloat(float min) throws CommandSyntaxException {
        return parseFloat(min, Float.MAX_VALUE);
    }

    public float parseFloat(float min, float max) throws CommandSyntaxException {
        float num = this.parseFloat();

        if (num < min) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.double.tooSmall", num, min));
            throw CommandExceptions.NUMBER_TOO_SMALL;
        }
        if (num > max) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.double.tooBig", num, max));
            throw CommandExceptions.NUMBER_TOO_BIG;
        }

        return num;
    }

    public float parseFloatOrDefault(float defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseFloat();
    }

    public float parseFloatOrDefault(float defaultValue, float min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseFloat(min);
    }

    public float parseFloatOrDefault(float defaultValue, float min, float max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseFloat(min, max);
    }

    public float parseFloatOrDefault(FloatSupplier defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsFloat();
        }
        return this.parseFloat();
    }

    public float parseFloatOrDefault(FloatSupplier defaultValue, float min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsFloat();
        }
        return this.parseFloat(min);
    }

    public float parseFloatOrDefault(FloatSupplier defaultValue, float min, float max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsFloat();
        }
        return this.parseFloat(min, max);
    }

    public double parseDouble() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw CommandExceptions.NOT_FLOAT;
        }
    }

    public double parseDouble(double min) throws CommandSyntaxException {
        return parseDouble(min, Double.MAX_VALUE);
    }

    public double parseDouble(double min, double max) throws CommandSyntaxException {
        double num = this.parseDouble();

        if (num < min) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.double.tooSmall", num, min));
            throw CommandExceptions.NUMBER_TOO_SMALL;
        }
        if (num > max) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.double.tooBig", num, max));
            throw CommandExceptions.NUMBER_TOO_BIG;
        }

        return num;
    }

    public double parseDoubleOrDefault(double defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseDouble();
    }

    public double parseDoubleOrDefault(double defaultValue, double min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseDouble(min);
    }

    public double parseDoubleOrDefault(double defaultValue, double min, double max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseDouble(min, max);
    }

    public double parseDoubleOrDefault(DoubleSupplier defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsDouble();
        }
        return this.parseDouble();
    }

    public double parseDoubleOrDefault(DoubleSupplier defaultValue, double min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsDouble();
        }
        return this.parseDouble(min);
    }

    public double parseDoubleOrDefault(DoubleSupplier defaultValue, double min, double max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsDouble();
        }
        return this.parseDouble(min, max);
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

    public boolean parseBooleanOrDefault(boolean defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseBoolean();
    }

    public boolean parseBooleanOrDefault(BooleanSupplier defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsBoolean();
        }
        return this.parseBoolean();
    }

    public String literal() throws CommandSyntaxException {
        return this.next();
    }

    public String literalOrDefault(String defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.literal();
    }

    public String literalOrDefault(Supplier<String> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.literal();
    }

    public <T> T parse(Function<String, T> parser) throws CommandSyntaxException {
        String arg = this.next();
        try {
            return parser.apply(arg);
        } catch (CommandSyntaxException e) {
            throw e;
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public <T> T parseOrDefault(T defaultValue, Function<String, T> parser) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parse(parser);
    }

    public <T> T parseOrDefault(Supplier<T> defaultValue, Function<String, T> parser) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parse(parser);
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType) throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public <T extends Enum<T>> T parseEnumOrDefault(T defaultValue, Class<T> enumType) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseEnum(enumType);
    }

    public <T extends Enum<T>> T parseEnumOrDefault(Supplier<T> defaultValue, Class<T> enumType) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseEnum(enumType);
    }

    public List<Entity> parseTargets() throws CommandSyntaxException {
        String arg = this.next();

        List<Entity> targets;
        try {
            targets = EntitySelector.matchEntities(this.sender, arg, true);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }

        if (targets.isEmpty()) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
            throw CommandExceptions.NO_TARGET;
        }

        return targets;
    }

    public List<Entity> parseTargets(int limit) throws CommandSyntaxException {
        List<Entity> targets = this.parseTargets();

        if (targets.size() > limit) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.tooManyTargets"));
            throw CommandExceptions.TOO_MANY_TARGETS;
        }

        return targets;
    }

    public List<Entity> parseTargetsOrDefault(List<Entity> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseTargets();
    }

    public List<Entity> parseTargetsOrDefault(Supplier<List<Entity>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargets();
    }

    public List<Entity> parseTargetsOrDefault(int limit, List<Entity> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseTargets(limit);
    }

    public List<Entity> parseTargetsOrDefault(int limit, Supplier<List<Entity>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargets(limit);
    }

    public List<Entity> parseTargetsOrSelf() throws CommandSyntaxException {
        return this.parseTargetsOrDefault(() -> {
            if (!(sender instanceof Entity)) {
                throw CommandExceptions.NO_TARGET;
            }
            return Collections.singletonList((Entity) sender);
        });
    }

    public List<Entity> parseTargetsOrSelf(int limit) throws CommandSyntaxException {
        return this.parseTargetsOrDefault(limit, () -> {
            if (!(sender instanceof Entity)) {
                throw CommandExceptions.NO_TARGET;
            }
            return Collections.singletonList((Entity) sender);
        });
    }

    public List<Player> parseTargetPlayers() throws CommandSyntaxException {
        List<Player> targets = this.parseTargets().stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());

        if (targets.isEmpty()) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
            throw CommandExceptions.NO_TARGET;
        }

        return targets;
    }

    public List<Player> parseTargetPlayers(int limit) throws CommandSyntaxException {
        List<Player> targets = this.parseTargetPlayers();

        if (targets.size() > limit) {
            this.setErrorMessage(new TranslationContainer("%commands.generic.tooManyTargets"));
            throw CommandExceptions.TOO_MANY_TARGETS;
        }

        return targets;
    }

    public List<Player> parseTargetPlayersOrDefault(List<Player> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseTargetPlayers();
    }

    public List<Player> parseTargetPlayersOrDefault(Supplier<List<Player>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargetPlayers();
    }

    public List<Player> parseTargetPlayersOrDefault(int limit, List<Player> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseTargetPlayers(limit);
    }

    public List<Player> parseTargetPlayersOrDefault(int limit, Supplier<List<Player>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargetPlayers(limit);
    }

    public List<Player> parseTargetPlayersOrSelf() throws CommandSyntaxException {
        return this.parseTargetPlayersOrDefault(() -> {
            if (!(sender instanceof Player)) {
                throw CommandExceptions.NO_TARGET;
            }
            return Collections.singletonList((Player) sender);
        });
    }

    public List<Player> parseTargetPlayersOrSelf(int limit) throws CommandSyntaxException {
        return this.parseTargetPlayersOrDefault(limit, () -> {
            if (!(sender instanceof Player)) {
                throw CommandExceptions.NO_TARGET;
            }
            return Collections.singletonList((Player) sender);
        });
    }

    public Vector3 parseVector3Target() throws CommandSyntaxException {
        this.mark();
        try {
            return this.parseVector3();
        } catch (CommandSyntaxException e) {
            if (e == CommandExceptions.NO_TARGET || e == CommandExceptions.TOO_MANY_TARGETS) {
                throw e;
            }
            this.reset();
        }
        return this.parseTargets(1).get(0);
    }

    public Vector3 parseVector3TargetOrDefault(Vector3 defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseVector3Target();
    }

    public Vector3 parseVector3TargetOrDefault(Supplier<Vector3> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseVector3Target();
    }

    public Position parsePositionTarget() throws CommandSyntaxException {
        Position pos = Position.fromObject(this.parseVector3Target());
        if (pos.level == null) {
            pos.level = this.getTargetLevel();
        }
        return pos;
    }

    public Position parsePositionTargetOrDefault(Vector3 defaultValue) throws CommandSyntaxException {
        Position pos = Position.fromObject(this.parseVector3TargetOrDefault(defaultValue));
        if (pos.level == null) {
            pos.level = this.getTargetLevel();
        }
        return pos;
    }

    public Position parsePositionTargetOrDefault(Supplier<Vector3> defaultValue) throws CommandSyntaxException {
        Position pos = Position.fromObject(this.parseVector3TargetOrDefault(defaultValue));
        if (pos.level == null) {
            pos.level = this.getTargetLevel();
        }
        return pos;
    }

    public Position parsePosition() throws CommandSyntaxException {
        return Position.fromObject(this.parseVector3(), this.getTargetLevel());
    }

    public Position parsePositionOrDefault(Position defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parsePosition();
    }

    public Position parsePositionOrDefault(Supplier<Position> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parsePosition();
    }

    public Position parsePositionOrSelf() throws CommandSyntaxException {
        return this.parsePositionOrDefault(() -> Position.fromObject(this.parseVector3OrSelf(), this.getTargetLevel()));
    }

    public Vector3 parseVector3() throws CommandSyntaxException {
        double baseX;
        double baseY;
        double baseZ;

        Vector3 vec = this.senderAsVector3();
        if (vec != null) {
            baseX = vec.getX();
            baseY = vec.getY();
            baseZ = vec.getZ();
        } else {
            baseX = 0;
            baseY = 0;
            baseZ = 0;
        }

        return new Vector3(this.parseCoordinate(baseX), this.parseCoordinate(baseY), this.parseCoordinate(baseZ));
    }

    public Vector3 parseVector3OrDefault(Vector3 defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseVector3();
    }

    public Vector3 parseVector3OrDefault(Supplier<Vector3> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseVector3();
    }

    public Vector3 parseVector3OrSelf() throws CommandSyntaxException {
        return this.parseVector3OrDefault(() -> {
            Vector3 vec = this.senderAsVector3();
            if (vec == null) {
                throw CommandExceptions.NO_TARGET;
            }
            return vec;
        });
    }

    public Vector2 parseVector2() throws CommandSyntaxException {
        double baseX;
        double baseZ;

        Vector2 vec = this.senderAsVector2();
        if (vec != null) {
            baseX = vec.getX();
            baseZ = vec.getY();
        } else {
            baseX = 0;
            baseZ = 0;
        }

        return new Vector2(this.parseCoordinate(baseX), this.parseCoordinate(baseZ));
    }

    public Vector2 parseVector2OrDefault(Vector2 defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseVector2();
    }

    public Vector2 parseVector2OrDefault(Supplier<Vector2> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseVector2();
    }

    public Vector2 parseVector2OrSelf() throws CommandSyntaxException {
        return this.parseVector2OrDefault(() -> {
            Vector2 vec = this.senderAsVector2();
            if (vec == null) {
                throw CommandExceptions.NO_TARGET;
            }
            return vec;
        });
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

    @Nullable
    public Vector3 senderAsVector3() {
        if (this.sender instanceof Vector3) {
            return (Vector3) this.sender;
        }
        if (this.sender instanceof Vector3f) {
            Vector3f vec = (Vector3f) this.sender;
            return new Vector3(vec.getX(), vec.getY(), vec.getZ());
        }
        if (this.sender instanceof BlockVector3) {
            BlockVector3 vec = (BlockVector3) this.sender;
            return new Vector3(vec.getX(), vec.getY(), vec.getZ());
        }
        return null;
    }

    @Nullable
    public Vector2 senderAsVector2() {
        if (this.sender instanceof Vector3) {
            Vector3 vec = (Vector3) this.sender;
            return new Vector2(vec.getX(), vec.getZ());
        }
        if (this.sender instanceof Vector3f) {
            Vector3f vec = (Vector3f) this.sender;
            return new Vector2(vec.getX(), vec.getZ());
        }
        if (this.sender instanceof BlockVector3) {
            BlockVector3 vec = (BlockVector3) this.sender;
            return new Vector2(vec.getX(), vec.getZ());
        }
        if (this.sender instanceof Vector2) {
            return (Vector2) this.sender;
        }
        return null;
    }

    public Block parseBlock() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Block.fromString(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public Block parseBlockOrDefault(Block defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseBlock();
    }

    public Block parseBlockOrDefault(Supplier<Block> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseBlock();
    }

    public Item parseItem() throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Item.fromString(arg);
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public Item parseItemOrDefault(Item defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue;
        }
        return this.parseItem();
    }

    public Item parseItemOrDefault(Supplier<Item> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseItem();
    }
}
