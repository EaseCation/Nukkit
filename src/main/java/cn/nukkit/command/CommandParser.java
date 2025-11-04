package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.block.state.*;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Effects;
import cn.nukkit.utils.OptionalFloat;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.function.FloatSupplier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
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

    public void clearMarker() {
        marker = -1;
    }

    public void clearCursor() {
        cursor = -1;
    }

    public void clear() {
        clearMarker();
        clearCursor();
        clearErrorMessage();
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
        return parseIntOrDefault(() -> defaultValue);
    }

    public int parseIntOrDefault(int defaultValue, int min) throws CommandSyntaxException {
        return parseIntOrDefault(() -> defaultValue, min);
    }

    public int parseIntOrDefault(int defaultValue, int min, int max) throws CommandSyntaxException {
        return parseIntOrDefault(() -> defaultValue, min, max);
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

    public OptionalInt parseIntOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.parseInt());
    }

    public OptionalInt parseIntOptional(int min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.parseInt(min));
    }

    public OptionalInt parseIntOptional(int min, int max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.parseInt(min, max));
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
        return parseLongOrDefault(() -> defaultValue);
    }

    public long parseLongOrDefault(long defaultValue, long min) throws CommandSyntaxException {
        return parseLongOrDefault(() -> defaultValue, min);
    }

    public long parseLongOrDefault(long defaultValue, long min, long max) throws CommandSyntaxException {
        return parseLongOrDefault(() -> defaultValue, min, max);
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

    public OptionalLong parseLongOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(this.parseLong());
    }

    public OptionalLong parseLongOptional(long min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(this.parseLong(min));
    }

    public OptionalLong parseLongOptional(long min, long max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(this.parseLong(min, max));
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
        return parseFloatOrDefault(() -> defaultValue);
    }

    public float parseFloatOrDefault(float defaultValue, float min) throws CommandSyntaxException {
        return parseFloatOrDefault(() -> defaultValue, min);
    }

    public float parseFloatOrDefault(float defaultValue, float min, float max) throws CommandSyntaxException {
        return parseFloatOrDefault(() -> defaultValue, min, max);
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

    public OptionalFloat parseFloatOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalFloat.empty();
        }
        return OptionalFloat.of(this.parseFloat());
    }

    public OptionalFloat parseFloatOptional(float min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalFloat.empty();
        }
        return OptionalFloat.of(this.parseFloat(min));
    }

    public OptionalFloat parseFloatOptional(float min, float max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalFloat.empty();
        }
        return OptionalFloat.of(this.parseFloat(min, max));
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
        return parseDoubleOrDefault(() -> defaultValue);
    }

    public double parseDoubleOrDefault(double defaultValue, double min) throws CommandSyntaxException {
        return parseDoubleOrDefault(() -> defaultValue, min);
    }

    public double parseDoubleOrDefault(double defaultValue, double min, double max) throws CommandSyntaxException {
        return parseDoubleOrDefault(() -> defaultValue, min, max);
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

    public OptionalDouble parseDoubleOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(this.parseDouble());
    }

    public OptionalDouble parseDoubleOptional(double min) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(this.parseDouble(min));
    }

    public OptionalDouble parseDoubleOptional(double min, double max) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(this.parseDouble(min, max));
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
        return parseBooleanOrDefault(() -> defaultValue);
    }

    public boolean parseBooleanOrDefault(BooleanSupplier defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.getAsBoolean();
        }
        return this.parseBoolean();
    }

    public Optional<Boolean> parseBooleanOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseBoolean());
    }

    public String literal() throws CommandSyntaxException {
        return this.next();
    }

    public String literalOrDefault(String defaultValue) throws CommandSyntaxException {
        return literalOrDefault(() -> defaultValue);
    }

    public String literalOrDefault(Supplier<String> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.literal();
    }

    public Optional<String> literalOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.literal());
    }

    @Nullable
    public String peek() {
        return peekOrDefault(() -> null);
    }

    public String peekOrDefault(String defaultValue) {
        return peekOrDefault(() -> defaultValue);
    }

    public String peekOrDefault(Supplier<String> defaultValue) {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.args[this.cursor + 1];
    }

    public Optional<String> peekOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.args[this.cursor + 1]);
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
        return parseOrDefault((Supplier<T>) () -> defaultValue, parser);
    }

    public <T> T parseOrDefault(Supplier<T> defaultValue, Function<String, T> parser) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parse(parser);
    }

    public <T> Optional<T> parseOptional(Function<String, T> parser) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.parse(parser));
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType) throws CommandSyntaxException {
        String arg = this.next();
        try {
            return Enum.valueOf(enumType, arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public <T extends Enum<T>> T parseEnumOrDefault(T defaultValue) throws CommandSyntaxException {
        return parseEnumOrDefault((Supplier<T>) () -> defaultValue, defaultValue.getDeclaringClass());
    }

    public <T extends Enum<T>> T parseEnumOrDefault(@Nullable T defaultValue, Class<T> enumType) throws CommandSyntaxException {
        return parseEnumOrDefault((Supplier<T>) () -> defaultValue, enumType);
    }

    public <T extends Enum<T>> T parseEnumOrDefault(Supplier<T> defaultValue, Class<T> enumType) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseEnum(enumType);
    }

    public <T extends Enum<T>> Optional<T> parseEnumOptional(Class<T> enumType) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseEnum(enumType));
    }

    public List<Entity> parseTargets() throws CommandSyntaxException {
        String arg = this.next();

        if (!arg.endsWith("]")) {
            String next = this.peek();
            if (next != null && next.length() > 2 && next.startsWith("[") && next.endsWith("]") && next.contains("=")) {
                arg += this.next(); // selector
            }
        }

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
        return parseTargetsOrDefault(() -> defaultValue);
    }

    public List<Entity> parseTargetsOrDefault(Supplier<List<Entity>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargets();
    }

    public List<Entity> parseTargetsOrDefault(int limit, List<Entity> defaultValue) throws CommandSyntaxException {
        return parseTargetsOrDefault(limit, () -> defaultValue);
    }

    public List<Entity> parseTargetsOrDefault(int limit, Supplier<List<Entity>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargets(limit);
    }

    public Optional<List<Entity>> parseTargetsOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseTargets());
    }

    public Optional<List<Entity>> parseTargetsOptional(int limit) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseTargets(limit));
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
        return parseTargetPlayersOrDefault(() -> defaultValue);
    }

    public List<Player> parseTargetPlayersOrDefault(Supplier<List<Player>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargetPlayers();
    }

    public List<Player> parseTargetPlayersOrDefault(int limit, List<Player> defaultValue) throws CommandSyntaxException {
        return parseTargetPlayersOrDefault(limit, () -> defaultValue);
    }

    public List<Player> parseTargetPlayersOrDefault(int limit, Supplier<List<Player>> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseTargetPlayers(limit);
    }

    public Optional<List<Player>> parseTargetPlayersOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseTargetPlayers());
    }

    public Optional<List<Player>> parseTargetPlayersOptional(int limit) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseTargetPlayers(limit));
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
        return this.parseTargets(1).getFirst();
    }

    public Vector3 parseVector3TargetOrDefault(Vector3 defaultValue) throws CommandSyntaxException {
        return parseVector3TargetOrDefault(() -> defaultValue);
    }

    public Vector3 parseVector3TargetOrDefault(Supplier<Vector3> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseVector3Target();
    }

    public Optional<Vector3> parseVector3TargetOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseVector3Target());
    }

    public Position parsePositionTarget() throws CommandSyntaxException {
        Position pos = Position.fromObject(this.parseVector3Target());
        if (pos.level == null) {
            pos.level = this.getTargetLevel();
        }
        return pos;
    }

    public Position parsePositionTargetOrDefault(Vector3 defaultValue) throws CommandSyntaxException {
        return parsePositionTargetOrDefault(() -> defaultValue);
    }

    public Position parsePositionTargetOrDefault(Supplier<Vector3> defaultValue) throws CommandSyntaxException {
        Position pos = Position.fromObject(this.parseVector3TargetOrDefault(defaultValue));
        if (pos.level == null) {
            pos.level = this.getTargetLevel();
        }
        return pos;
    }

    public Optional<Position> parsePositionTargetOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parsePositionTarget());
    }

    public Position parsePosition() throws CommandSyntaxException {
        return Position.fromObject(this.parseVector3(), this.getTargetLevel());
    }

    public Position parsePositionOrDefault(Position defaultValue) throws CommandSyntaxException {
        return parsePositionOrDefault(() -> defaultValue);
    }

    public Position parsePositionOrDefault(Supplier<Position> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parsePosition();
    }

    public Optional<Position> parsePositionOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parsePosition());
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
        return parseVector3OrDefault(() -> defaultValue);
    }

    public Vector3 parseVector3OrDefault(Supplier<Vector3> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseVector3();
    }

    public Optional<Vector3> parseVector3Optional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseVector3());
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
        return parseVector2OrDefault(() -> defaultValue);
    }

    public Vector2 parseVector2OrDefault(Supplier<Vector2> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseVector2();
    }

    public Optional<Vector2> parseVector2Optional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseVector2());
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

    public Optional<Vector3> senderAsVector3Optional() throws CommandSyntaxException {
        return Optional.ofNullable(this.senderAsVector3());
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

    public Optional<Vector2> senderAsVector2Optional() throws CommandSyntaxException {
        return Optional.ofNullable(this.senderAsVector2());
    }

    public Block parseBlock() throws CommandSyntaxException {
        String arg = this.next();

        String blockName = arg;
        String states = null;
        if (!arg.endsWith("]")) {
            String next = this.peek();
            if (next != null && next.length() >= 2 && next.startsWith("[") && next.endsWith("]")) {
                states = this.next();
            }
        } else {
            int index = arg.indexOf("[");
            if (index > 0) {
                blockName = arg.substring(0, index);
                states = arg.substring(index);
            } else {
                throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
            }
        }

        try {
            Block block = Block.fromStringNullable(blockName.toLowerCase(), true);
            if (block == null) {
                throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
            }
            if (states != null) {
                applyBlockStates(block, parseBlockStates(states));
            }
            return block;
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public Block parseBlockOrDefault(Block defaultValue) throws CommandSyntaxException {
        return parseBlockOrDefault(() -> defaultValue);
    }

    public Block parseBlockOrDefault(Supplier<Block> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseBlock();
    }

    public Optional<Block> parseBlockOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseBlock());
    }

    private Map<String, Object> parseBlockStates(String str) throws CommandSyntaxException {
        if (str.length() < 2) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
        try {
            str = str.substring(1, str.length() - 1).strip();
            if (str.isBlank()) {
                return Collections.emptyMap();
            }
            Map<String, Object> blockStates = new HashMap<>();
            for (String pair : str.split(",", 16)) {
                String[] kv = pair.split("=", 2);
                if (kv.length != 2) {
                    throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                }
                String name = kv[0].strip();
                String valueStr = kv[1].strip();
                Object value = valueStr;
                if ("true".equalsIgnoreCase(valueStr)) {
                    value = Boolean.TRUE;
                } else if ("false".equalsIgnoreCase(valueStr)) {
                    value = Boolean.FALSE;
                } else {
                    try {
                        value = Integer.valueOf(valueStr);
                    } catch (NumberFormatException ignored) {
                    }
                }
                blockStates.put(name, value);
            }
            return blockStates;
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    private void applyBlockStates(Block block, Map<String, Object> states) throws CommandSyntaxException {
        for (Entry<String, Object> entry : states.entrySet()) {
            String name = entry.getKey();
            List<BlockState> stateList = BlockStates.get(name);
            if (stateList == null) {
                setErrorMessage(new TranslationContainer("%commands.blockstate.invalidState", name));
                throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
            }
            BlockState state = stateList.getFirst();
            if (!block.hasState(state)) {
                //TODO: backward compatibility
                setErrorMessage(new TranslationContainer("%commands.blockstate.stateError", name, Blocks.getBlockNameById(block.getId())));
                throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
            }
            Object value = entry.getValue();
            switch (value) {
                case Boolean bool -> {
                    if (!(state instanceof BooleanBlockState boolState)) {
                        setErrorMessage(new TranslationContainer("%commands.blockstate.typeError", name));
                        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                    }
                    block.setState(boolState, bool);
                }
                case Number num -> {
                    if (!(state instanceof IntegerBlockState intState)) {
                        setErrorMessage(new TranslationContainer("%commands.blockstate.typeError", name));
                        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                    }
                    block.setState(intState, num.intValue());
                }
                default -> {
                    if (state instanceof StringBlockState strState) {
                        block.setState(strState, String.valueOf(value));
                    } else if (state instanceof EnumBlockState enumState) {
                        Enum<?> element = enumState.get(String.valueOf(value));
                        if (element == null) {
                            setErrorMessage(new TranslationContainer("%commands.blockstate.valueError", name));
                            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                        }
                        block.setState(enumState, element);
                    }
                }
            }
        }
    }

    public Item parseItem() throws CommandSyntaxException {
        String arg = this.next();
        try {
            Item item = Item.fromStringNullable(arg.toLowerCase(), true);
            if (item == null) {
                throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
            }
            return item;
        } catch (Exception e) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
    }

    public Item parseItemOrDefault(Item defaultValue) throws CommandSyntaxException {
        return parseItemOrDefault(() -> defaultValue);
    }

    public Item parseItemOrDefault(Supplier<Item> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseItem();
    }

    public Optional<Item> parseItemOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseItem());
    }

    public Biome parseBiome() throws CommandSyntaxException {
        String arg = this.next();
        Biome biome;
        try {
            biome = Biomes.getNullable(Integer.parseInt(arg));
        } catch (NumberFormatException a) {
            biome = Biomes.getNullable(arg.toLowerCase());
        }
        if (biome == null) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
        return biome;
    }

    public Biome parseBiomeOrDefault(Biome defaultValue) throws CommandSyntaxException {
        return parseBiomeOrDefault(() -> defaultValue);
    }

    public Biome parseBiomeOrDefault(Supplier<Biome> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseBiome();
    }

    public Optional<Biome> parseBiomeOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseBiome());
    }

    public Enchantment parseEnchantment() throws CommandSyntaxException {
        String arg = this.next();
        Enchantment enchantment;
        try {
            enchantment = Enchantment.getEnchantment(Integer.parseInt(arg));
        } catch (NumberFormatException a) {
            enchantment = Enchantments.getEnchantmentByIdentifier(arg.toLowerCase());
        }
        if (enchantment == null) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
        return enchantment;
    }

    public Enchantment parseEnchantmentOrDefault(Enchantment defaultValue) throws CommandSyntaxException {
        return parseEnchantmentOrDefault(() -> defaultValue);
    }

    public Enchantment parseEnchantmentOrDefault(Supplier<Enchantment> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseEnchantment();
    }

    public Optional<Enchantment> parseEnchantmentOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseEnchantment());
    }

    public Effect parseEffect() throws CommandSyntaxException {
        String arg = this.next();
        Effect effect;
        try {
            effect = Effect.getEffect(Integer.parseInt(arg));
        } catch (NumberFormatException a) {
            effect = Effects.getEffectByIdentifier(arg.toLowerCase());
        }
        if (effect == null) {
            throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
        }
        return effect;
    }

    public Effect parseEffectOrDefault(Effect defaultValue) throws CommandSyntaxException {
        return parseEffectOrDefault(() -> defaultValue);
    }

    public Effect parseEffectOrDefault(Supplier<Effect> defaultValue) throws CommandSyntaxException {
        if (!this.hasNext()) {
            return defaultValue.get();
        }
        return this.parseEffect();
    }

    public Optional<Effect> parseEffectOptional() throws CommandSyntaxException {
        if (!this.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(this.parseEffect());
    }
}
