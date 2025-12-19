package cn.nukkit.utils;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BannerPattern {

    private final Type type;
    private final DyeColor color;

    public BannerPattern(Type type, DyeColor color) {
        this.type = type;
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public Type getType() {
        return this.type;
    }

    public static BannerPattern fromCompoundTag(CompoundTag compoundTag) {
        return new BannerPattern(Type.getByName(compoundTag.contains("Pattern") ? compoundTag.getString("Pattern") : ""), compoundTag.contains("Color") ? DyeColor.getByDyeData(compoundTag.getInt("Color")) : DyeColor.BLACK);
    }

    public enum Type {
        PATTERN_BOTTOM_STRIPE("bs"),
        PATTERN_TOP_STRIPE("ts"),
        PATTERN_LEFT_STRIPE("ls"),
        PATTERN_RIGHT_STRIPE("rs"),
        PATTERN_CENTER_STRIPE("cs"),
        PATTERN_MIDDLE_STRIPE("ms"),
        PATTERN_DOWN_RIGHT_STRIPE("drs"),
        PATTERN_DOWN_LEFT_STRIPE("dls"),
        PATTERN_SMALL_STRIPES("ss"),
        PATTERN_DIAGONAL_CROSS("cr"),
        PATTERN_SQUARE_CROSS("sc"),
        PATTERN_LEFT_OF_DIAGONAL("ld"),
        PATTERN_RIGHT_OF_UPSIDE_DOWN_DIAGONAL("rud"),
        PATTERN_LEFT_OF_UPSIDE_DOWN_DIAGONAL("lud"),
        PATTERN_RIGHT_OF_DIAGONAL("rd"),
        PATTERN_VERTICAL_HALF_LEFT("vh"),
        PATTERN_VERTICAL_HALF_RIGHT("vhr"),
        PATTERN_HORIZONTAL_HALF_TOP("hh"),
        PATTERN_HORIZONTAL_HALF_BOTTOM("hhb"),
        PATTERN_BOTTOM_LEFT_CORNER("bl"),
        PATTERN_BOTTOM_RIGHT_CORNER("br"),
        PATTERN_TOP_LEFT_CORNER("tl"),
        PATTERN_TOP_RIGHT_CORNER("tr"),
        PATTERN_BOTTOM_TRIANGLE("bt"),
        PATTERN_TOP_TRIANGLE("tt"),
        PATTERN_BOTTOM_TRIANGLE_SAWTOOTH("bts"),
        PATTERN_TOP_TRIANGLE_SAWTOOTH("tts"),
        PATTERN_MIDDLE_CIRCLE("mc"),
        PATTERN_MIDDLE_RHOMBUS("mr"),
        PATTERN_BORDER("bo"),
        PATTERN_CURLY_BORDER("cbo", Item.BORDURE_INDENTED_BANNER_PATTERN),
        PATTERN_BRICK("bri", Item.FIELD_MASONED_BANNER_PATTERN),
        PATTERN_GRADIENT("gra"),
        PATTERN_GRADIENT_UPSIDE_DOWN("gru"),
        PATTERN_CREEPER("cre", Item.CREEPER_BANNER_PATTERN),
        PATTERN_SKULL("sku", Item.SKULL_BANNER_PATTERN),
        PATTERN_FLOWER("flo", Item.FLOWER_BANNER_PATTERN),
        PATTERN_MOJANG("moj", Item.MOJANG_BANNER_PATTERN),
        PATTERN_PIGLIN("pig", Item.PIGLIN_BANNER_PATTERN),
        PATTERN_GLOBE("glb", Item.GLOBE_BANNER_PATTERN),
        PATTERN_FLOW("flw", Item.FLOW_BANNER_PATTERN),
        PATTERN_GUSTER("gus", Item.GUSTER_BANNER_PATTERN),
        ;

        private final static Map<String, Type> BY_NAME = new HashMap<>();
        private final static Int2ObjectMap<Type> BY_ID = new Int2ObjectOpenHashMap<>();

        private final String name;
        private final int id;

        Type(String name) {
            this(name, -1);
        }

        Type(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return id;
        }

        static {
            for (Type type : values()) {
                BY_NAME.put(type.getName(), type);
                if (type.id != -1) {
                    BY_ID.put(type.id, type);
                }
            }
        }

        @Nullable
        public static Type getByName(String name) {
            return BY_NAME.get(name);
        }

        @Nullable
        public static Type getById(int id) {
            return BY_ID.get(id);
        }
    }

}
