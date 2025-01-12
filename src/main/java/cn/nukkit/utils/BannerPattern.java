package cn.nukkit.utils;

import cn.nukkit.item.ItemBannerPattern;
import cn.nukkit.nbt.tag.CompoundTag;

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
        PATTERN_CURLY_BORDER("cbo", ItemBannerPattern.BORDURE_INDENTED_BANNER_PATTERN),
        PATTERN_BRICK("bri", ItemBannerPattern.FIELD_MASONED_BANNER_PATTERN),
        PATTERN_GRADIENT("gra"),
        PATTERN_GRADIENT_UPSIDE_DOWN("gru"),
        PATTERN_CREEPER("cre", ItemBannerPattern.CREEPER_BANNER_PATTERN),
        PATTERN_SKULL("sku", ItemBannerPattern.SKULL_BANNER_PATTERN),
        PATTERN_FLOWER("flo", ItemBannerPattern.FLOWER_BANNER_PATTERN),
        PATTERN_MOJANG("moj", ItemBannerPattern.MOJANG_BANNER_PATTERN),
        PATTERN_PIGLIN("pig", ItemBannerPattern.CREEPER_BANNER_PATTERN),
        PATTERN_GLOBE("glb", ItemBannerPattern.CREEPER_BANNER_PATTERN),
        PATTERN_FLOW("flw", ItemBannerPattern.CREEPER_BANNER_PATTERN),
        PATTERN_GUSTER("gus", ItemBannerPattern.CREEPER_BANNER_PATTERN),
        ;

        private final static Map<String, Type> BY_NAME = new HashMap<>();
        private final static Type[] BY_META = new Type[ItemBannerPattern.UNDEFINED_BANNER_PATTERN];

        private final String name;
        private final int meta;

        Type(String name) {
            this(name, -1);
        }

        Type(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        public String getName() {
            return this.name;
        }

        public int getMeta() {
            return meta;
        }

        static {
            for (Type type : values()) {
                BY_NAME.put(type.getName(), type);
                if (type.meta != -1) {
                    BY_META[type.meta] = type;
                }
            }
        }

        @Nullable
        public static Type getByName(String name) {
            return BY_NAME.get(name);
        }

        @Nullable
        public static Type getByMeta(int meta) {
            if (meta < 0 || meta >= BY_META.length) {
                return null;
            }
            return BY_META[meta];
        }
    }

}
