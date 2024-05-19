package cn.nukkit.inventory;

import cn.nukkit.block.BlockID;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @since 1.11.0
 */
public enum RecipeTag {
    CRAFTING_TABLE("crafting_table", BlockID.CRAFTING_TABLE),
    CARTOGRAPHY_TABLE("cartography_table", BlockID.CARTOGRAPHY_TABLE),
    STONECUTTER("stonecutter", BlockID.STONECUTTER_BLOCK),
    FURNACE("furnace", BlockID.FURNACE, BlockID.LIT_FURNACE),
    CAMPFIRE("campfire", BlockID.BLOCK_CAMPFIRE),
    BLAST_FURNACE("blast_furnace", BlockID.BLAST_FURNACE, BlockID.LIT_BLAST_FURNACE),
    SMOKER("smoker", BlockID.SMOKER, BlockID.LIT_SMOKER),
    SOUL_CAMPFIRE("soul_campfire", BlockID.BLOCK_SOUL_CAMPFIRE),
    SMITHING_TABLE("smithing_table", BlockID.SMITHING_TABLE),
    DEPRECATED("deprecated"),
    ;

    private static final RecipeTag[] VALUES = values();
    private static final Map<String, RecipeTag> BY_NAME = new Object2ObjectOpenHashMap<>();

    static {
        for (RecipeTag tag : VALUES) {
            BY_NAME.put(tag.name, tag);
        }
    }

    private final String name;
    private final IntList validBlocks;

    RecipeTag(String name, int... validBlocks) {
        this.name = name;
        this.validBlocks = IntArrayList.wrap(validBlocks);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public IntList getValidBlocks() {
        return this.validBlocks;
    }

    public boolean isValidBlock(int blockId) {
        return this.validBlocks.contains(blockId);
    }

    @Nullable
    public static RecipeTag byName(String name) {
        return BY_NAME.get(name);
    }

    public static RecipeTag[] getValues() {
        return VALUES;
    }
}
