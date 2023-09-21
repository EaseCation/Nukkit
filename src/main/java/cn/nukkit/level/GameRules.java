package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;

import static cn.nukkit.level.GameRule.*;

@SuppressWarnings({"unchecked"})
public class GameRules {
    private final EnumMap<GameRule, Value> gameRules = new EnumMap<>(GameRule.class);
    private boolean stale;

    private GameRules() {
    }

    public static GameRules getDefault() {
        GameRules gameRules = new GameRules();

        gameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_DAYLIGHT_CYCLE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_ENTITY_DROPS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_FIRE_TICK, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_IMMEDIATE_RESPAWN, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_MOB_LOOT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_MOB_SPAWNING, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_TILE_DROPS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_WEATHER_CYCLE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DROWNING_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FALL_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FIRE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(KEEP_INVENTORY, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(MOB_GRIEFING, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(NATURAL_REGENERATION, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(PVP, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(RANDOM_TICK_SPEED, new Value<>(Type.INTEGER, 1));
        gameRules.gameRules.put(SEND_COMMAND_FEEDBACK, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_COORDINATES, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(TNT_EXPLODES, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_DEATH_MESSAGES, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(MAX_COMMAND_CHAIN_LENGTH, new Value<>(Type.INTEGER, 65535));
        gameRules.gameRules.put(DO_INSOMNIA, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(COMMAND_BLOCKS_ENABLED, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FUNCTION_COMMAND_LIMIT, new Value<>(Type.INTEGER, 10000));
        gameRules.gameRules.put(SPAWN_RADIUS, new Value<>(Type.INTEGER, 0));
        gameRules.gameRules.put(SHOW_TAGS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FREEZE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(RESPAWN_BLOCKS_EXPLODE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_BORDER_EFFECT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(RECIPES_UNLOCK, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(DO_LIMITED_CRAFTING, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(PLAYERS_SLEEPING_PERCENTAGE, new Value<>(Type.INTEGER, 100));

        return gameRules;
    }

    public Map<GameRule, Value> getGameRules() {
        return new EnumMap<>(gameRules);
    }

    public Map<GameRule, Value> getGameRulesUnsafe() {
        return gameRules;
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }

    public void setGameRule(GameRule gameRule, boolean value) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        gameRules.get(gameRule).setValue(value, Type.BOOLEAN);
        stale = true;
    }

    public void setGameRule(GameRule gameRule, int value) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        gameRules.get(gameRule).setValue(value, Type.INTEGER);
        stale = true;
    }

    public void setGameRule(GameRule gameRule, float value) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        gameRules.get(gameRule).setValue(value, Type.FLOAT);
        stale = true;
    }

    public void setGameRules(GameRule gameRule, String value) throws IllegalArgumentException {
        Preconditions.checkNotNull(gameRule, "gameRule");
        Preconditions.checkNotNull(value, "value");

        switch (getGameRuleType(gameRule)) {
            case BOOLEAN:
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
                    setGameRule(gameRule, true);
                } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
                    setGameRule(gameRule, false);
                } else {
                    throw new IllegalArgumentException("Was not a boolean");
                }
                break;
            case INTEGER:
                setGameRule(gameRule, Integer.parseInt(value));
                break;
            case FLOAT:
                setGameRule(gameRule, Float.parseFloat(value));
        }
    }

    public boolean getBoolean(GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsBoolean();
    }

    public int getInteger(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsInteger();
    }

    public float getFloat(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsFloat();
    }

    public String getString(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).value.toString();
    }

    public Type getGameRuleType(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getType();
    }

    public boolean hasRule(GameRule gameRule) {
        return gameRules.containsKey(gameRule);
    }

    public GameRule[] getRules() {
        return gameRules.keySet().toArray(new GameRule[0]);
    }

    public void writeNBT(CompoundTag nbt) {
        for (Entry<GameRule, Value> entry : gameRules.entrySet()) {
            nbt.putString(entry.getKey().getName(), entry.getValue().value.toString());
        }
    }

    public void readNBT(CompoundTag nbt) {
        Preconditions.checkNotNull(nbt);
        for (String key : nbt.getTags().keySet()) {
            Optional<GameRule> gameRule = GameRule.parseString(key);
            if (gameRule.isEmpty()) {
                continue;
            }

            try {
                setGameRules(gameRule.get(), nbt.getString(key));
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
    }

    public void writeBedrockNBT(CompoundTag nbt) {
        gameRules.forEach((gameRule, value) -> {
            String name = gameRule.getBedrockName();
            switch (value.type) {
                case BOOLEAN:
                    nbt.putBoolean(name, value.getValueAsBoolean());
                    break;
                case INTEGER:
                    nbt.putInt(name, value.getValueAsInteger());
                    break;
                case FLOAT:
                    nbt.putFloat(name, value.getValueAsFloat());
                    break;
                case UNKNOWN:
                default:
                    nbt.putString(name, value.value.toString());
                    break;
            }
        });
    }

    public void readBedrockNBT(CompoundTag nbt) {
        gameRules.forEach((gameRule, value) -> {
            String name = gameRule.getBedrockName();
            Tag tag = nbt.get(name);
            if (tag == null) {
                return;
            }

            switch (value.type) {
                case BOOLEAN:
                    if (tag instanceof ByteTag) {
                        setGameRule(gameRule, ((ByteTag) tag).data != 0);
                    } else if (tag instanceof StringTag) {
                        String data = ((StringTag) tag).data;
                        if (data.equalsIgnoreCase("true") || data.equals("1")) {
                            setGameRule(gameRule, true);
                        } else if (data.equalsIgnoreCase("false") || data.equals("0")) {
                            setGameRule(gameRule, false);
                        } else {
                            Server.getInstance().getLogger().warning("Invalid boolean game rule '" + name + "' value: " + data);
                        }
                    }
                    break;
                case INTEGER:
                    if (tag instanceof IntTag) {
                        setGameRule(gameRule, ((IntTag) tag).data);
                    } else if (tag instanceof StringTag) {
                        String data = ((StringTag) tag).data;
                        try {
                            setGameRule(gameRule, Integer.parseInt(data));
                        } catch (Exception e) {
                            Server.getInstance().getLogger().warning("Invalid integer game rule '" + name + "' value: " + data);
                        }
                    }
                    break;
                case FLOAT:
                    if (tag instanceof FloatTag) {
                        setGameRule(gameRule, ((FloatTag) tag).data);
                    } else if (tag instanceof StringTag) {
                        String data = ((StringTag) tag).data;
                        try {
                            setGameRule(gameRule, Float.parseFloat(data));
                        } catch (Exception e) {
                            Server.getInstance().getLogger().warning("Invalid float game rule '" + name + "' value: " + data);
                        }
                    }
                    break;
            }
        });
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        gameRules.forEach((rule, value) -> joiner.add(rule.getBedrockName() + " = " + value.value.toString()));
        return joiner.toString();
    }

    public enum Type {
        UNKNOWN {
            @Override
            void write(BinaryStream pk, Value value) {
            }
        },
        BOOLEAN {
            @Override
            void write(BinaryStream pk, Value value) {
                pk.putBoolean(value.getValueAsBoolean());
            }
        },
        INTEGER {
            @Override
            void write(BinaryStream pk, Value value) {
                pk.putUnsignedVarInt(value.getValueAsInteger());
            }
        },
        FLOAT {
            @Override
            void write(BinaryStream pk, Value value) {
                pk.putLFloat(value.getValueAsFloat());
            }
        };

        abstract void write(BinaryStream pk, Value value);
    }

    public static class Value<T> {
        private final Type type;
        private T value;

        public Value(Type type, T value) {
            this.type = type;
            this.value = value;
        }

        private void setValue(T value, Type type) {
            if (this.type != type) {
                throw new UnsupportedOperationException("Rule not of type " + type.name().toLowerCase());
            }
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        private boolean getValueAsBoolean() {
            if (type != Type.BOOLEAN) {
                throw new UnsupportedOperationException("Rule not of type boolean");
            }
            return (Boolean) value;
        }

        private int getValueAsInteger() {
            if (type != Type.INTEGER) {
                throw new UnsupportedOperationException("Rule not of type integer");
            }
            return (Integer) value;
        }

        private float getValueAsFloat() {
            if (type != Type.FLOAT) {
                throw new UnsupportedOperationException("Rule not of type float");
            }
            return (Float) value;
        }

        public void write(BinaryStream pk) {
            pk.putUnsignedVarInt(type.ordinal());
            type.write(pk, this);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
