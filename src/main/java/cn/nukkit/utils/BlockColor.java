package cn.nukkit.utils;

import java.awt.Color;

/**
 * Created by Snake1999 on 2016/1/10.
 * Package cn.nukkit.utils in project nukkit
 */
public class BlockColor extends Color {

    public static final BlockColor TRANSPARENT_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00, 0x00);
    public static final BlockColor VOID_BLOCK_COLOR = TRANSPARENT_BLOCK_COLOR;

    public static final BlockColor AIR_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00);
    public static final BlockColor GRASS_BLOCK_COLOR = new BlockColor(0x7f, 0xb2, 0x38);
    public static final BlockColor SAND_BLOCK_COLOR = new BlockColor(0xf7, 0xe9, 0xa3);
    public static final BlockColor WOOL_BLOCK_COLOR = new BlockColor(0xc7, 0xc7, 0xc7);
    public static final BlockColor FIRE_BLOCK_COLOR = new BlockColor(0xff, 0x00, 0x00);
    public static final BlockColor ICE_BLOCK_COLOR = new BlockColor(0xa0, 0xa0, 0xff);
    public static final BlockColor METAL_BLOCK_COLOR = new BlockColor(0xa7, 0xa7, 0xa7);
    public static final BlockColor PLANT_BLOCK_COLOR = new BlockColor(0x00, 0x7c, 0x00);
    public static final BlockColor SNOW_BLOCK_COLOR = new BlockColor(0xff, 0xff, 0xff);
    public static final BlockColor CLAY_BLOCK_COLOR = new BlockColor(0xa4, 0xa8, 0xb8);
    public static final BlockColor DIRT_BLOCK_COLOR = new BlockColor(0x97, 0x6d, 0x4d);
    public static final BlockColor STONE_BLOCK_COLOR = new BlockColor(0x70, 0x70, 0x70);
    public static final BlockColor WATER_BLOCK_COLOR = new BlockColor(0x40, 0x40, 0xff);
    public static final BlockColor WOOD_BLOCK_COLOR = new BlockColor(0x8f, 0x77, 0x48);
    public static final BlockColor QUARTZ_BLOCK_COLOR = new BlockColor(0xff, 0xfc, 0xf5);

    public static final BlockColor ORANGE_BLOCK_COLOR = new BlockColor(0xd8, 0x7f, 0x33);
    public static final BlockColor MAGENTA_BLOCK_COLOR = new BlockColor(0xb2, 0x4c, 0xd8);
    public static final BlockColor LIGHT_BLUE_BLOCK_COLOR = new BlockColor(0x66, 0x99, 0xd8);
    public static final BlockColor YELLOW_BLOCK_COLOR = new BlockColor(0xe5, 0xe5, 0x33);
    public static final BlockColor LIME_BLOCK_COLOR = new BlockColor(0x7f, 0xcc, 0x19);
    public static final BlockColor PINK_BLOCK_COLOR = new BlockColor(0xf2, 0x7f, 0xa5);
    public static final BlockColor GRAY_BLOCK_COLOR = new BlockColor(0x4c, 0x4c, 0x4c);
    public static final BlockColor LIGHT_GRAY_BLOCK_COLOR = new BlockColor(0x99, 0x99, 0x99);
    public static final BlockColor CYAN_BLOCK_COLOR = new BlockColor(0x4c, 0x7f, 0x99);
    public static final BlockColor PURPLE_BLOCK_COLOR = new BlockColor(0x7f, 0x3f, 0xb2);
    public static final BlockColor BLUE_BLOCK_COLOR = new BlockColor(0x33, 0x4c, 0xb2);
    public static final BlockColor BROWN_BLOCK_COLOR = new BlockColor(0x66, 0x4c, 0x33);
    public static final BlockColor GREEN_BLOCK_COLOR = new BlockColor(0x66, 0x7f, 0x33);
    public static final BlockColor RED_BLOCK_COLOR = new BlockColor(0x99, 0x33, 0x33);
    public static final BlockColor BLACK_BLOCK_COLOR = new BlockColor(0x19, 0x19, 0x19);
    public static final BlockColor GOLD_BLOCK_COLOR = new BlockColor(0xfa, 0xee, 0x4d);
    public static final BlockColor DIAMOND_BLOCK_COLOR = new BlockColor(0x5c, 0xdb, 0xd5);
    public static final BlockColor LAPIS_BLOCK_COLOR = new BlockColor(0x4a, 0x80, 0xff);
    public static final BlockColor EMERALD_BLOCK_COLOR = new BlockColor(0x00, 0xd9, 0x3a);
    public static final BlockColor PODZOL_BLOCK_COLOR = new BlockColor(0x81, 0x56, 0x31);
    public static final BlockColor NETHER_BLOCK_COLOR = new BlockColor(0x70, 0x02, 0x00);
    public static final BlockColor WHITE_TERRACOTA_BLOCK_COLOR = new BlockColor(0xd1, 0xb1, 0xa1);
    public static final BlockColor ORANGE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x9f, 0x52, 0x24);
    public static final BlockColor MAGENTA_TERRACOTA_BLOCK_COLOR = new BlockColor(0x95, 0x57, 0x6c);
    public static final BlockColor LIGHT_BLUE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x70, 0x6c, 0x8a);
    public static final BlockColor YELLOW_TERRACOTA_BLOCK_COLOR = new BlockColor(0xba, 0x85, 0x24);
    public static final BlockColor LIME_TERRACOTA_BLOCK_COLOR = new BlockColor(0x67, 0x75, 0x35);
    public static final BlockColor PINK_TERRACOTA_BLOCK_COLOR = new BlockColor(0xa0, 0x4d, 0x4e);
    public static final BlockColor GRAY_TERRACOTA_BLOCK_COLOR = new BlockColor(0x39, 0x29, 0x23);
    public static final BlockColor LIGHT_GRAY_TERRACOTA_BLOCK_COLOR = new BlockColor(0x87, 0x6b, 0x62);
    public static final BlockColor CYAN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x57, 0x5c, 0x5c);
    public static final BlockColor PURPLE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x7a, 0x49, 0x58);
    public static final BlockColor BLUE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x3e, 0x5c);
    public static final BlockColor BROWN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x32, 0x23);
    public static final BlockColor GREEN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x52, 0x2a);
    public static final BlockColor RED_TERRACOTA_BLOCK_COLOR = new BlockColor(0x8e, 0x3c, 0x2e);
    public static final BlockColor BLACK_TERRACOTA_BLOCK_COLOR = new BlockColor(0x25, 0x16, 0x10);
    public static final BlockColor CRIMSON_NYLIUM_BLOCK_COLOR = new BlockColor(0xbd, 0x30, 0x31);
    public static final BlockColor CRIMSON_STEM_BLOCK_COLOR = new BlockColor(0x94, 0x3f, 0x61);
    public static final BlockColor CRIMSON_HYPHAE_BLOCK_COLOR = new BlockColor(0x5c, 0x19, 0x1d);
    public static final BlockColor WARPED_NYLIUM_BLOCK_COLOR = new BlockColor(0x16, 0x7e, 0x86);
    public static final BlockColor WARPED_STEM_BLOCK_COLOR = new BlockColor(0x3a, 0x8e, 0x8c);
    public static final BlockColor WARPED_HYPHAE_BLOCK_COLOR = new BlockColor(0x56, 0x2c, 0x3e);
    public static final BlockColor WARPED_WART_BLOCK_BLOCK_COLOR = new BlockColor(0x14, 0xb4, 0x85);
    public static final BlockColor SCULK_BLOCK_COLOR = new BlockColor(0x0d, 0x12, 0x17);
    public static final BlockColor DEEPSLATE_BLOCK_COLOR = new BlockColor(0x64, 0x64, 0x64);
    public static final BlockColor RAW_IRON_BLOCK_COLOR = new BlockColor(0xd8, 0xaf, 0x93);
    public static final BlockColor GLOW_LICHEN_BLOCK_COLOR = new BlockColor(0x7f, 0xa7, 0x96);

    public BlockColor(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public BlockColor(float r, float g, float b) {
        super(r, g, b);
    }

    public BlockColor(int rgba, boolean hasAlpha) {
        super(rgba, hasAlpha);
    }

    public BlockColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public BlockColor(int rgb) {
        super(rgb);
    }

    public BlockColor(int r, int g, int b) {
        super(r, g, b);
    }

    public static int getRGB(int r, int g, int b) {
        return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    public static int getARGB(int r, int g, int b) {
        return getARGB(r, g, b, 0xff);
    }

    public static int getARGB(int r, int g, int b, int a) {
        return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }
}
