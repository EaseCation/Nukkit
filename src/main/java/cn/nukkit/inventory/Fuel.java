package cn.nukkit.inventory;

import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;

import static cn.nukkit.block.Block.getItemId;
import static cn.nukkit.item.ItemID.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Fuel {
    public static final Int2ShortMap duration = new Int2ShortOpenHashMap();

    static {
        duration.defaultReturnValue((short) -1);

        duration.put(COAL, (short) 1600);
        duration.put(getItemId(COAL_BLOCK), (short) 16000);
        duration.put(getItemId(LOG), (short) 300);
        duration.put(getItemId(MANGROVE_LOG), (short) 300);
        duration.put(getItemId(PLANKS), (short) 300);
        duration.put(getItemId(MANGROVE_PLANKS), (short) 300);
        duration.put(getItemId(SAPLING), (short) 100);
        duration.put(getItemId(MANGROVE_PROPAGULE), (short) 100);
        duration.put(WOODEN_AXE, (short) 200);
        duration.put(WOODEN_PICKAXE, (short) 200);
        duration.put(WOODEN_SWORD, (short) 200);
        duration.put(WOODEN_SHOVEL, (short) 200);
        duration.put(WOODEN_HOE, (short) 200);
        duration.put(STICK, (short) 100);
        duration.put(getItemId(FENCE), (short) 300);
        duration.put(getItemId(MANGROVE_FENCE), (short) 300);
        duration.put(getItemId(FENCE_GATE), (short) 300);
        duration.put(getItemId(SPRUCE_FENCE_GATE), (short) 300);
        duration.put(getItemId(BIRCH_FENCE_GATE), (short) 300);
        duration.put(getItemId(JUNGLE_FENCE_GATE), (short) 300);
        duration.put(getItemId(ACACIA_FENCE_GATE), (short) 300);
        duration.put(getItemId(MANGROVE_FENCE_GATE), (short) 300);
        duration.put(getItemId(DARK_OAK_FENCE_GATE), (short) 300);
        duration.put(getItemId(OAK_STAIRS), (short) 300);
        duration.put(getItemId(SPRUCE_STAIRS), (short) 300);
        duration.put(getItemId(BIRCH_STAIRS), (short) 300);
        duration.put(getItemId(JUNGLE_STAIRS), (short) 300);
        duration.put(getItemId(TRAPDOOR), (short) 300);
        duration.put(getItemId(MANGROVE_TRAPDOOR), (short) 300);
        duration.put(getItemId(CRAFTING_TABLE), (short) 300);
        duration.put(getItemId(BOOKSHELF), (short) 300);
        duration.put(getItemId(CHEST), (short) 300);
        duration.put(BUCKET, (short) 20000);
        duration.put(getItemId(LADDER), (short) 300);
        duration.put(BOW, (short) 200);
        duration.put(BOWL, (short) 200);
        duration.put(getItemId(LOG2), (short) 300);
        duration.put(getItemId(WOODEN_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(SPRUCE_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(BIRCH_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(JUNGLE_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(ACACIA_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(DARK_OAK_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(MANGROVE_PRESSURE_PLATE), (short) 300);
        duration.put(getItemId(ACACIA_STAIRS), (short) 300);
        duration.put(getItemId(DARK_OAK_STAIRS), (short) 300);
        duration.put(getItemId(MANGROVE_STAIRS), (short) 300);
        duration.put(getItemId(TRAPPED_CHEST), (short) 300);
        duration.put(getItemId(DAYLIGHT_DETECTOR), (short) 300);
        duration.put(getItemId(DAYLIGHT_DETECTOR_INVERTED), (short) 300);
        duration.put(getItemId(JUKEBOX), (short) 300);
        duration.put(getItemId(NOTEBLOCK), (short) 300);
        duration.put(getItemId(WOODEN_SLAB), (short) 300);
        duration.put(getItemId(MANGROVE_SLAB), (short) 300);
        duration.put(getItemId(DOUBLE_WOODEN_SLAB), (short) 300);
        duration.put(getItemId(MANGROVE_DOUBLE_SLAB), (short) 300);
        duration.put(BOAT, (short) 1200);
//        duration.put(CHEST_BOAT, (short) 1200);
        duration.put(BLAZE_ROD, (short) 2400);
        duration.put(getItemId(BROWN_MUSHROOM_BLOCK), (short) 300);
        duration.put(getItemId(RED_MUSHROOM_BLOCK), (short) 300);
        duration.put(FISHING_ROD, (short) 300);
        duration.put(getItemId(WOODEN_BUTTON), (short) 300);
        duration.put(getItemId(SPRUCE_BUTTON), (short) 300);
        duration.put(getItemId(BIRCH_BUTTON), (short) 300);
        duration.put(getItemId(JUNGLE_BUTTON), (short) 300);
        duration.put(getItemId(ACACIA_BUTTON), (short) 300);
        duration.put(getItemId(DARK_OAK_BUTTON), (short) 300);
        duration.put(getItemId(MANGROVE_BUTTON), (short) 300);
        duration.put(WOODEN_DOOR, (short) 200);
        duration.put(SPRUCE_DOOR, (short) 200);
        duration.put(BIRCH_DOOR, (short) 200);
        duration.put(JUNGLE_DOOR, (short) 200);
        duration.put(ACACIA_DOOR, (short) 200);
        duration.put(DARK_OAK_DOOR, (short) 200);
        duration.put(MANGROVE_DOOR, (short) 200);
        duration.put(BANNER, (short) 300);
        duration.put(getItemId(DEADBUSH), (short) 100);
        duration.put(OAK_SIGN, (short) 200);
        duration.put(SPRUCE_SIGN, (short) 200);
        duration.put(BIRCH_SIGN, (short) 200);
        duration.put(JUNGLE_SIGN, (short) 200);
        duration.put(ACACIA_SIGN, (short) 200);
        duration.put(DARK_OAK_SIGN, (short) 200);
        duration.put(MANGROVE_SIGN, (short) 200);
        duration.put(getItemId(DRIED_KELP_BLOCK), (short) 4000);
        duration.put(getItemId(SCAFFOLDING), (short) 1200);
        duration.put(getItemId(BEE_NEST), (short) 300);
        duration.put(getItemId(BEEHIVE), (short) 300);
        duration.put(getItemId(STRIPPED_OAK_LOG), (short) 300);
        duration.put(getItemId(STRIPPED_SPRUCE_LOG), (short) 300);
        duration.put(getItemId(STRIPPED_BIRCH_LOG), (short) 300);
        duration.put(getItemId(STRIPPED_JUNGLE_LOG), (short) 300);
        duration.put(getItemId(STRIPPED_ACACIA_LOG), (short) 300);
        duration.put(getItemId(STRIPPED_DARK_OAK_LOG), (short) 300);
        duration.put(getItemId(STRIPPED_MANGROVE_LOG), (short) 300);
        duration.put(getItemId(WOOD), (short) 300);
        duration.put(getItemId(MANGROVE_WOOD), (short) 300);
        duration.put(getItemId(STRIPPED_MANGROVE_WOOD), (short) 300);
        duration.put(getItemId(MANGROVE_ROOTS), (short) 300);
        duration.put(getItemId(CARTOGRAPHY_TABLE), (short) 300);
        duration.put(getItemId(FLETCHING_TABLE), (short) 300);
        duration.put(getItemId(SMITHING_TABLE), (short) 300);
        duration.put(getItemId(LOOM), (short) 300);
        duration.put(getItemId(LECTERN), (short) 300);
        duration.put(getItemId(COMPOSTER), (short) 300);
        duration.put(getItemId(BARREL), (short) 300);
        duration.put(CROSSBOW, (short) 200);
        duration.put(getItemId(AZALEA), (short) 100);
        duration.put(getItemId(FLOWERING_AZALEA), (short) 100);
        duration.put(getItemId(BAMBOO), (short) 50);
    }
}
