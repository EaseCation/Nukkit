package cn.nukkit.data;

import cn.nukkit.Server;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ItemIdMap {
    private static final Object2IntMap<String> NAME_TO_ID;
    private static final String[] ID_TO_NAME = new String[65536];

    @Nullable
    public static String getNameById(int id) {
        return ID_TO_NAME[id & 0xffff];
    }

    public static int getIdByName(String name) {
        return NAME_TO_ID.getInt(name);
    }

    static {
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("item_id_map.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            NAME_TO_ID = new Gson().fromJson(reader, Object2IntOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load item_id_map.json", e);
        }
        NAME_TO_ID.forEach((name, id) -> ID_TO_NAME[id & 0xffff] = name);
        NAME_TO_ID.defaultReturnValue(Integer.MAX_VALUE);
    }

    public static void init() {
    }

    private ItemIdMap() {
    }
}
