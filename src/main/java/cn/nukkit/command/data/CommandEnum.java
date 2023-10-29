package cn.nukkit.command.data;

import cn.nukkit.GameMode;
import cn.nukkit.block.Blocks;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.potion.Effects;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
public class CommandEnum {

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableMap.of(
            "true", Collections.emptySet(),
            "false", Collections.emptySet()));
    public static final CommandEnum ENUM_GAMEMODE;
    public static final CommandEnum ENUM_BLOCK;
    public static final CommandEnum ENUM_ITEM;
    public static final CommandEnum ENUM_ENTITY_TYPE;
    public static final CommandEnum ENUM_ENCHANT = new CommandEnum("Enchant", Enchantments.getEnchantments().keySet());
    public static final CommandEnum ENUM_EFFECT = new CommandEnum("Effect", Effects.getEffects().keySet());

    static {
        ImmutableMap.Builder<String, Set<CommandEnumConstraint>> gameModes = ImmutableMap.builder();
        for (GameMode gameMode : GameMode.getValues()) {
            gameModes.put(gameMode.getIdentifier(), Collections.emptySet());
            for (String alias : gameMode.getAliases()) {
                gameModes.put(alias, Collections.emptySet());
            }
        }
        ENUM_GAMEMODE = new CommandEnum("GameMode", gameModes.build());

        ImmutableMap.Builder<String, Set<CommandEnumConstraint>> blocks = ImmutableMap.builder();
        for (String blockName : Blocks.getBlockNameToIdMap().keySet()) {
            String name = blockName.toLowerCase();
            if (!blockName.equals(name)) {
                continue;
            }
            blocks.put(name, Collections.emptySet());
            blocks.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String blockName : Blocks.getBlockAliasesMap().keySet()) {
            String name = blockName.toLowerCase();
            if (!blockName.equals(name)) {
                continue;
            }
            blocks.put(name, Collections.emptySet());
            blocks.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String blockName : Blocks.getComplexAliasesMap().keySet()) {
            String name = blockName.toLowerCase();
            if (!blockName.equals(name)) {
                continue;
            }
            blocks.put(name, Collections.emptySet());
            blocks.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        ENUM_BLOCK = new CommandEnum("Block", blocks.build());

        ImmutableMap.Builder<String, Set<CommandEnumConstraint>> items = ImmutableMap.builder();
        for (String name : Blocks.getBlockItemNameToIdMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Blocks.getItemAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Blocks.getComplexAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Items.getNameToIdMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Items.getSimpleAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Items.getComplexAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        ENUM_ITEM = new CommandEnum("Item", items.build());

        ImmutableMap.Builder<String, Set<CommandEnumConstraint>> entities = ImmutableMap.builder();
        for (Field field : EntityID.class.getDeclaredFields()) {
            String name = field.getName().toLowerCase();
            entities.put(name, Collections.emptySet());
            entities.put("minecraft:" + name, Collections.emptySet());
        }
        ENUM_ENTITY_TYPE = new CommandEnum("EntityType", entities.build());
    }

    private final String name;
    private final Map<String, Set<CommandEnumConstraint>> values;

    public boolean soft;

    public CommandEnum(String name, Enum<?>... values) {
        this(name, false, Arrays.stream(values)
                .map(value -> Pair.of(value.toString().toLowerCase(), EnumSet.noneOf(CommandEnumConstraint.class)))
                .collect(Collectors.toMap(Pair::left, Pair::right)));
    }

    public CommandEnum(String name, String... values) {
        this(name, false, values);
    }

    public CommandEnum(String name, boolean soft, String... values) {
        this(name, soft, Arrays.stream(values)
                .map(value -> Pair.of(value.toLowerCase(), EnumSet.noneOf(CommandEnumConstraint.class)))
                .collect(Collectors.toMap(Pair::left, Pair::right)));
    }

    public CommandEnum(String name, Collection<String> values) {
        this(name, false, values);
    }

    public CommandEnum(String name, boolean soft, Collection<String> values) {
        this(name, soft, values.stream()
                .map(value -> Pair.of(value.toLowerCase(), EnumSet.noneOf(CommandEnumConstraint.class)))
                .collect(Collectors.toMap(Pair::left, Pair::right)));
    }

    public CommandEnum(String name, Map<String, Set<CommandEnumConstraint>> values) {
        this(name, false, values);
    }

    public CommandEnum(String name, boolean soft, Map<String, Set<CommandEnumConstraint>> values) {
        this.name = name;
        this.values = values;
        this.soft = soft;
    }

    public String getName() {
        return name;
    }

    public Map<String, Set<CommandEnumConstraint>> getValues() {
        return values;
    }

    public boolean isSoft() {
        return soft;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
