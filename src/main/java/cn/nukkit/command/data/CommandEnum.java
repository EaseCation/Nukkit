package cn.nukkit.command.data;

import cn.nukkit.GameMode;
import cn.nukkit.block.Blocks;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.property.EntityProperty;
import cn.nukkit.entity.property.EntityPropertyRegistry;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.potion.Effects;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.Pair;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author CreeperFace
 */
@ToString
public class CommandEnum {

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableMap.of(
            "true", Collections.emptySet(),
            "false", Collections.emptySet()));
    public static final CommandEnum ENUM_GAMEMODE;
    public static final CommandEnum ENUM_BLOCK;
    public static final CommandEnum ENUM_ITEM;
    public static final CommandEnum ENUM_ENTITY_TYPE;
    public static final CommandEnum ENUM_ENTITY_PROPERTY = new CommandEnum("EntityProperty", EntityPropertyRegistry.getRegistry().values().stream()
            .flatMap(properties -> StreamSupport.stream(properties.spliterator(), false).map(EntityProperty::getName))
            .collect(Collectors.toSet()));
    public static final CommandEnum ENUM_ENCHANT = new CommandEnum("Enchant", Enchantments.getEnchantments().keySet());
    public static final CommandEnum ENUM_EFFECT = new CommandEnum("Effect", Effects.getEffects().keySet());
    public static final CommandEnum ENUM_BIOME;
    public static final CommandEnum ENUM_DIMENSION = new CommandEnum("Dimension", Dimension.values());

    static {
        ImmutableMap.Builder<String, Set<CommandEnumConstraint>> gameModes = ImmutableMap.builder();
        for (GameMode gameMode : GameMode.getValues()) {
            gameModes.put(gameMode.getIdentifier(), Collections.emptySet());
            for (String alias : gameMode.getAliases()) {
                gameModes.put(alias, Collections.emptySet());
            }
        }
        ENUM_GAMEMODE = new CommandEnum("GameMode", gameModes.build());

        Map<String, Set<CommandEnumConstraint>> blocks = new HashMap<>();
        for (String blockName : Blocks.getBlockNameToIdMap().keySet()) {
            String name = blockName.toLowerCase();
            if (!blockName.equals(name)) {
                continue;
            }
            blocks.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            blocks.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String blockName : Blocks.getBlockAliasesMap().keySet()) {
            String name = blockName.toLowerCase();
            if (!blockName.equals(name)) {
                continue;
            }
            blocks.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            blocks.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String blockName : Blocks.getComplexAliasesMap().keySet()) {
            String name = blockName.toLowerCase();
            if (!blockName.equals(name)) {
                continue;
            }
            blocks.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            blocks.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        ENUM_BLOCK = new CommandEnum("Block", blocks);

        Map<String, Set<CommandEnumConstraint>> items = new HashMap<>();
        for (String name : Blocks.getBlockItemNameToIdMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Blocks.getItemAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Blocks.getComplexAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Items.getNameToIdMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Items.getSimpleAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        for (String name : Items.getComplexAliasesMap().keySet()) {
            if (name.contains(".")) {
                continue;
            }
            items.put(name, Collections.emptySet());
            if (name.indexOf(':') != -1) {
                continue;
            }
            items.put("minecraft:" + name, EnumSet.of(CommandEnumConstraint.ALLOW_ALIASES));
        }
        ENUM_ITEM = new CommandEnum("Item", items);

        Map<String, Set<CommandEnumConstraint>> entities = new HashMap<>();
        for (Field field : EntityID.class.getDeclaredFields()) {
            String name = field.getName().toLowerCase();
            entities.put(name, Collections.emptySet());
            entities.put("minecraft:" + name, Collections.emptySet());
        }
        ENUM_ENTITY_TYPE = new CommandEnum("EntityType", entities);

        Map<String, Set<CommandEnumConstraint>> biomes = new HashMap<>();
        for (Biome biome : Biomes.getBiomes()) {
            biomes.put(biome.getIdentifier(), Collections.emptySet());
            biomes.put(biome.getFullIdentifier(), Collections.emptySet());
        }
        ENUM_BIOME = new CommandEnum("Biome", biomes);
    }

    private final String name;
    private final Map<String, Set<CommandEnumConstraint>> values;

    public boolean soft;

    public CommandEnum(String name, Enum<?>... values) {
        this(name, false, Arrays.stream(values)
                .map(value -> Pair.of(value.name().toLowerCase(), EnumSet.noneOf(CommandEnumConstraint.class)))
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
