package cn.nukkit.data;

import cn.nukkit.Server;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public final class BlockItemConverter {
    private static final Map<String, String> BLOCK_NAME_TO_ITEM_NAME;
    private static final Map<String, String> ITEM_NAME_TO_BLOCK_NAME;

    @Nullable
    public static String getItemNameByBlockName(String blockName) {
        return BLOCK_NAME_TO_ITEM_NAME.get(blockName);
    }

    @Nullable
    public static String getBlockNameByItemName(String itemName) {
        return ITEM_NAME_TO_BLOCK_NAME.get(itemName);
    }

    static {
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_name_to_item_name.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            BLOCK_NAME_TO_ITEM_NAME = new Gson().fromJson(reader, Object2ObjectOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_name_to_item_name.json", e);
        }
        ITEM_NAME_TO_BLOCK_NAME = new Object2ObjectOpenHashMap<>();
        BLOCK_NAME_TO_ITEM_NAME.forEach((block, item) -> ITEM_NAME_TO_BLOCK_NAME.put(item, block));
    }

    public static void init() {
    }

    private BlockItemConverter() {
    }
}
