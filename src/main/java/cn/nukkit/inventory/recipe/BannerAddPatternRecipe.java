package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.transaction.FakeBlockTransactionHelper;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBanner;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.BannerPattern.Type;
import cn.nukkit.utils.DyeColor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BannerAddPatternRecipe extends MultiRecipe {
    private static final Int2ObjectMap<Type> PATTERNS = new Int2ObjectOpenHashMap<>();

    public BannerAddPatternRecipe(UUID id) {
        super(id, RecipeTag.CRAFTING_TABLE, Item.get(ItemID.BANNER));
    }

    @Override
    public List<Item> getResults(List<Item> inputs, int repetitions) {
        return getResults(inputs, repetitions, null);
    }

    @Override
    public List<Item> getResults(List<Item> inputs, int repetitions, @Nullable Item[] grid) {
        if (repetitions < 1 || repetitions > 256) {
            return List.of();
        }
        ItemBanner banner = null;
        Type materialPattern = null;
        DyeColor color = null;
        int dyes = 0;
        for (Item input : inputs) {
            if (input.getCount() <= 0 || input.getCount() % repetitions != 0) {
                return List.of();
            }
            if (input instanceof ItemBanner value && banner == null && input.getCount() == repetitions) {
                banner = value;
            } else if (input instanceof ItemDye dye) {
                if (color != null && color != dye.getDyeColor()) {
                    return List.of();
                }
                color = dye.getDyeColor();
                if (input.getCount() > repetitions * 8 - dyes) {
                    return List.of();
                }
                dyes += input.getCount();
            } else if (materialPattern == null && input.getCount() == repetitions && patternForMaterial(input) != null) {
                materialPattern = patternForMaterial(input);
            } else {
                return List.of();
            }
        }
        if (banner == null || color == null || banner.getPatternsSize() >= 6) {
            return List.of();
        }
        Type pattern;
        if (materialPattern != null) {
            if (dyes != repetitions) {
                return List.of();
            }
            pattern = materialPattern;
        } else {
            if (grid == null || grid.length != 9) {
                return List.of();
            }
            int mask = 0;
            List<Item> consumed = new ArrayList<>();
            for (int slot = 0; slot < grid.length; slot++) {
                Item item = grid[slot];
                if (item == null || item.isNull()) {
                    continue;
                }
                if (item.getCount() < repetitions) {
                    return List.of();
                }
                if (item instanceof ItemDye) {
                    mask |= 1 << slot;
                }
                consumed.add(item.clone(repetitions));
            }
            if (!FakeBlockTransactionHelper.matchesItems(inputs, consumed)) {
                return List.of();
            }
            pattern = PATTERNS.get(mask);
            if (pattern == null) {
                return List.of();
            }
        }
        ItemBanner result = (ItemBanner) banner.clone(repetitions);
        result.addPattern(new BannerPattern(pattern, color));
        return List.of(result);
    }

    @Override
    public List<Item> getResultsForGrid(Item[] grid) {
        if (grid.length != 9) {
            return List.of();
        }
        List<Item> inputs = new ArrayList<>();
        for (Item item : grid) {
            if (!item.isNull()) {
                inputs.add(item.clone(1));
            }
        }
        return getResults(inputs, 1, grid);
    }

    @Override
    public boolean match(List<Item> inputs, List<Item> outputs, int repetitions, @Nullable Item[] grid) {
        List<Item> expected = getResults(inputs, repetitions, grid);
        return !expected.isEmpty() && FakeBlockTransactionHelper.matchesItems(expected, outputs);
    }

    @Nullable
    private static Type patternForMaterial(Item material) {
        return switch (material.getId()) {
            case ItemID.BRICK -> Type.PATTERN_BRICK;
            case ItemID.CREEPER_HEAD -> Type.PATTERN_CREEPER;
            case ItemID.WITHER_SKELETON_SKULL -> Type.PATTERN_SKULL;
            case ItemID.VINE -> Type.PATTERN_CURLY_BORDER;
            case ItemID.OXEYE_DAISY -> Type.PATTERN_FLOWER;
            case ItemID.ENCHANTED_GOLDEN_APPLE -> Type.PATTERN_MOJANG;
            default -> null;
        };
    }

    private static void register(Type type, String first, String second, String third) {
        String shape = first + second + third;
        int mask = 0;
        for (int slot = 0; slot < shape.length(); slot++) {
            if (shape.charAt(slot) == '#') {
                mask |= 1 << slot;
            }
        }
        PATTERNS.put(mask, type);
    }

    static {
        register(Type.PATTERN_BORDER,
                "###",
                "# #",
                "###");
        register(Type.PATTERN_MIDDLE_CIRCLE,
                "   ",
                " # ",
                "   ");
        register(Type.PATTERN_DIAGONAL_CROSS,
                "# #",
                " # ",
                "# #");
        register(Type.PATTERN_LEFT_OF_DIAGONAL,
                "## ",
                "#  ",
                "   ");
        register(Type.PATTERN_RIGHT_OF_UPSIDE_DOWN_DIAGONAL,
                " ##",
                "  #",
                "   ");
        register(Type.PATTERN_LEFT_OF_UPSIDE_DOWN_DIAGONAL,
                "   ",
                "#  ",
                "## ");
        register(Type.PATTERN_RIGHT_OF_DIAGONAL,
                "   ",
                "  #",
                " ##");
        register(Type.PATTERN_GRADIENT,
                "# #",
                " # ",
                " # ");
        register(Type.PATTERN_GRADIENT_UPSIDE_DOWN,
                " # ",
                " # ",
                "# #");
        register(Type.PATTERN_HORIZONTAL_HALF_TOP,
                "###",
                "###",
                "   ");
        register(Type.PATTERN_HORIZONTAL_HALF_BOTTOM,
                "   ",
                "###",
                "###");
        register(Type.PATTERN_VERTICAL_HALF_LEFT,
                "## ",
                "## ",
                "## ");
        register(Type.PATTERN_VERTICAL_HALF_RIGHT,
                " ##",
                " ##",
                " ##");
        register(Type.PATTERN_MIDDLE_RHOMBUS,
                " # ",
                "# #",
                " # ");
        register(Type.PATTERN_SMALL_STRIPES,
                "# #",
                "# #",
                "   ");
        register(Type.PATTERN_BOTTOM_LEFT_CORNER,
                "   ",
                "   ",
                "#  ");
        register(Type.PATTERN_BOTTOM_RIGHT_CORNER,
                "   ",
                "   ",
                "  #");
        register(Type.PATTERN_TOP_LEFT_CORNER,
                "#  ",
                "   ",
                "   ");
        register(Type.PATTERN_TOP_RIGHT_CORNER,
                "  #",
                "   ",
                "   ");
        register(Type.PATTERN_SQUARE_CROSS,
                " # ",
                "###",
                " # ");
        register(Type.PATTERN_BOTTOM_STRIPE,
                "   ",
                "   ",
                "###");
        register(Type.PATTERN_CENTER_STRIPE,
                " # ",
                " # ",
                " # ");
        register(Type.PATTERN_DOWN_LEFT_STRIPE,
                "  #",
                " # ",
                "#  ");
        register(Type.PATTERN_DOWN_RIGHT_STRIPE,
                "#  ",
                " # ",
                "  #");
        register(Type.PATTERN_LEFT_STRIPE,
                "#  ",
                "#  ",
                "#  ");
        register(Type.PATTERN_MIDDLE_STRIPE,
                "   ",
                "###",
                "   ");
        register(Type.PATTERN_RIGHT_STRIPE,
                "  #",
                "  #",
                "  #");
        register(Type.PATTERN_TOP_STRIPE,
                "###",
                "   ",
                "   ");
        register(Type.PATTERN_BOTTOM_TRIANGLE,
                "   ",
                " # ",
                "# #");
        register(Type.PATTERN_TOP_TRIANGLE,
                "# #",
                " # ",
                "   ");
        register(Type.PATTERN_BOTTOM_TRIANGLE_SAWTOOTH,
                "   ",
                "# #",
                " # ");
        register(Type.PATTERN_TOP_TRIANGLE_SAWTOOTH,
                " # ",
                "# #",
                "   ");
    }
}
