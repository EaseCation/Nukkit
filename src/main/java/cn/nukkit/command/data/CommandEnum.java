package cn.nukkit.command.data;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author CreeperFace
 */
public class CommandEnum {

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableSet.of("true", "false"));
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableSet.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));
    public static final CommandEnum ENUM_BLOCK;
    public static final CommandEnum ENUM_ITEM;

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
    }

    private final String name;
    private final Set<String> values;

    public boolean soft;

    public CommandEnum(String name, String... values) {
        this(name, false, values);
    }

    public CommandEnum(String name, boolean soft, String... values) {
        this(name, soft, new HashSet<>(Arrays.asList(values)));
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