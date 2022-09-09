package cn.nukkit.command.data;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.EnchantmentID;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
public class CommandEnum {

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableSet.of("true", "false"));
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableSet.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));
    public static final CommandEnum ENUM_BLOCK;
    public static final CommandEnum ENUM_ITEM;
    public static final CommandEnum ENUM_ENTITY_TYPE;
    public static final CommandEnum ENUM_ENCHANT;

    static {
        ImmutableSet.Builder<String> blocks = ImmutableSet.builder();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase());
        }
        ENUM_BLOCK = new CommandEnum("Block", blocks.build());

        ImmutableSet.Builder<String> items = ImmutableSet.builder();
        for (Field field : ItemID.class.getDeclaredFields()) {
            items.add(field.getName().toLowerCase());
        }
        items.addAll(ENUM_BLOCK.getValues());
        ENUM_ITEM = new CommandEnum("Item", items.build());

        ImmutableSet.Builder<String> entities = ImmutableSet.builder();
        for (Field field : EntityID.class.getDeclaredFields()) {
            entities.add(field.getName().toLowerCase());
        }
        ENUM_ENTITY_TYPE = new CommandEnum("EntityType", entities.build());

        ImmutableSet.Builder<String> enchants = ImmutableSet.builder();
        for (Field field : EnchantmentID.class.getDeclaredFields()) {
            enchants.add(field.getName().substring(3).toLowerCase());
        }
        ENUM_ENCHANT = new CommandEnum("Enchant", enchants.build());
    }

    private final String name;
    private final Set<String> values;

    public boolean soft;

    public CommandEnum(String name, Enum<?>... values) {
        this(name, false, Arrays.stream(values)
                .map(value -> value.toString().toLowerCase())
                .collect(Collectors.toSet()));
    }

    public CommandEnum(String name, String... values) {
        this(name, false, values);
    }

    public CommandEnum(String name, boolean soft, String... values) {
        this(name, soft, new ObjectOpenHashSet<>(Arrays.asList(values)));
    }

    public CommandEnum(String name, Set<String> values) {
        this(name, false, values);
    }

    public CommandEnum(String name, boolean soft, Set<String> values) {
        this.name = name;
        this.values = values;
        this.soft = soft;
    }

    public String getName() {
        return name;
    }

    public Set<String> getValues() {
        return values;
    }

    public boolean isSoft() {
        return soft;
    }

    public int hashCode() {
        return name.hashCode();
    }
}