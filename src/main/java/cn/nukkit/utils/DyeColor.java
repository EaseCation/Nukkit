package cn.nukkit.utils;

import com.google.common.base.CaseFormat;

public enum DyeColor {
    BLACK(0, 15, 16, "Black", "Ink Sac", "black", "black_new", BlockColor.BLACK_BLOCK_COLOR, new BlockColor(0x00, 0x00, 0x00)),
    RED(1, 14, "Red", "red", BlockColor.RED_BLOCK_COLOR, new BlockColor(0xb0, 0x2e, 0x26)),
    GREEN(2, 13, "Green", "green", BlockColor.GREEN_BLOCK_COLOR, new BlockColor(0x5e, 0x7c, 0x16)),
    BROWN(3, 12, 17, "Brown", "Cocoa Beans", "brown", "brown_new", BlockColor.BROWN_BLOCK_COLOR, new BlockColor(0x83, 0x54, 0x32)),
    BLUE(4, 11, 18, "Blue", "Lapis Lazuli", "blue", "blue_new", BlockColor.BLUE_BLOCK_COLOR, new BlockColor(0x3c, 0x44, 0xaa)),
    PURPLE(5, 10, "Purple", "purple", BlockColor.PURPLE_BLOCK_COLOR, new BlockColor(0x89, 0x32, 0xb8)),
    CYAN(6, 9, "Cyan", "cyan", BlockColor.CYAN_BLOCK_COLOR, new BlockColor(0x16, 0x9c, 0x9c)),
    LIGHT_GRAY(7, 8, "Light Gray", "silver", BlockColor.LIGHT_GRAY_BLOCK_COLOR, new BlockColor(0x9d, 0x9d, 0x97)),
    GRAY(8, 7, "Gray", "gray", BlockColor.GRAY_BLOCK_COLOR, new BlockColor(0x47, 0x4f, 0x52)),
    PINK(9, 6, "Pink", "pink", BlockColor.PINK_BLOCK_COLOR, new BlockColor(0xf3, 0x8b, 0xaa)),
    LIME(10, 5, "Lime", "lime", BlockColor.LIME_BLOCK_COLOR, new BlockColor(0x80, 0xc7, 0x1f)),
    YELLOW(11, 4, "Yellow", "yellow", BlockColor.YELLOW_BLOCK_COLOR, new BlockColor(0xfe, 0xd8, 0x3d)),
    LIGHT_BLUE(12, 3, "Light Blue", "lightBlue", BlockColor.LIGHT_BLUE_BLOCK_COLOR, new BlockColor(0x3a, 0xb3, 0xda)),
    MAGENTA(13, 2, "Magenta", "magenta", BlockColor.MAGENTA_BLOCK_COLOR, new BlockColor(0xc7, 0x4e, 0xbd)),
    ORANGE(14, 1, "Orange", "orange", BlockColor.ORANGE_BLOCK_COLOR, new BlockColor(0xf9, 0x80, 0x1d)),
    WHITE(15, 0, 19, "White", "Bone Meal", "white", "white_new", BlockColor.SNOW_BLOCK_COLOR, new BlockColor(0xf0, 0xf0, 0xf0));

    private final int dyeColorMeta;
    private final int woolColorMeta;
    private final int newDyeColorMeta;
    private final String colorName;
    private final String dyeName;
    private final String descriptionName;
    private final String descriptionNameSnakeCase;
    private final String descriptionNamePascalCase;
    private final String dyeDescriptionName;
    private final BlockColor blockColor;
    private final BlockColor signColor;

    private final static DyeColor[] BY_WOOL_DATA;
    private final static DyeColor[] BY_DYE_DATA;
    private final static DyeColor[] BY_DYE_NEW_DATA;

    DyeColor(int dyeColorMeta, int woolColorMeta, String colorName, String descriptionName, BlockColor blockColor, BlockColor signColor) {
        this(dyeColorMeta, woolColorMeta, dyeColorMeta, colorName, colorName + " Dye", descriptionName, descriptionName, blockColor, signColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int newDyeColorMeta, String colorName, String dyeName, String descriptionName, String dyeDescriptionName, BlockColor blockColor, BlockColor signColor) {
        this.dyeColorMeta = dyeColorMeta;
        this.woolColorMeta = woolColorMeta;
        this.newDyeColorMeta = newDyeColorMeta;
        this.colorName = colorName;
        this.dyeName = dyeName;
        this.descriptionName = descriptionName;
        this.descriptionNameSnakeCase = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, descriptionName);
        this.descriptionNamePascalCase = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, descriptionName);
        this.dyeDescriptionName = dyeDescriptionName;
        this.blockColor = blockColor;
        this.signColor = signColor;
    }

    public BlockColor getColor() {
        return this.blockColor;
    }

    public BlockColor getSignColor() {
        return this.signColor;
    }

    public int getDyeData() {
        return this.dyeColorMeta;
    }

    public int getDyeNewData() {
        return this.newDyeColorMeta;
    }

    public int getWoolData() {
        return this.woolColorMeta;
    }

    public String getName() {
        return this.colorName;
    }

    public String getDyeName() {
        return this.dyeName;
    }

    /**
     * @return camelCase
     */
    public String getDescriptionName() {
        return descriptionName;
    }

    /**
     * @return snake_case
     */
    public String getDescriptionNameSnakeCase() {
        return descriptionNameSnakeCase;
    }

    /**
     * @return PascalCase
     */
    public String getDescriptionNamePascalCase() {
        return descriptionNamePascalCase;
    }

    public String getDyeDescriptionName() {
        return dyeDescriptionName;
    }

    static {
        BY_DYE_DATA = values();
        BY_WOOL_DATA = values();
        BY_DYE_NEW_DATA = new DyeColor[BY_DYE_DATA.length + 4];

        for (DyeColor color : values()) {
            BY_WOOL_DATA[color.woolColorMeta & 0x0f] = color;
            BY_DYE_DATA[color.dyeColorMeta & 0x0f] = color;

            BY_DYE_NEW_DATA[color.dyeColorMeta & 0x0f] = color;
            BY_DYE_NEW_DATA[color.newDyeColorMeta] = color;
        }
    }

    public static DyeColor getByDyeData(int dyeColorMeta) {
        return BY_DYE_DATA[dyeColorMeta & 0x0f];
    }

    public static DyeColor getByDyeNewData(int dyeMeta) {
        if (dyeMeta < 0 || dyeMeta >= BY_DYE_NEW_DATA.length) {
            dyeMeta = 0;
        }
        return BY_DYE_NEW_DATA[dyeMeta];
    }

    public static DyeColor getByWoolData(int woolColorMeta) {
        return BY_WOOL_DATA[woolColorMeta & 0x0f];
    }

    private static final DyeColor[] VALUES = values();

    public static DyeColor[] getValues() {
        return VALUES;
    }
}
