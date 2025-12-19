package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.utils.BinaryStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RuntimeItems implements RuntimeItemPaletteInterface {

    private static RuntimeItemPaletteInterface instance = new RuntimeItems();

    public static void setInstance(RuntimeItemPaletteInterface instance) {
        RuntimeItems.instance = instance;
    }

    private static final Gson GSON = new Gson();
    private static final Type ENTRY_TYPE = new TypeToken<ArrayList<Entry>>(){}.getType();

    private static final Int2IntMap LEGACY_NETWORK_MAP = new Int2IntOpenHashMap();
    private static final Int2IntMap NETWORK_LEGACY_MAP = new Int2IntOpenHashMap();

    public static byte[] ITEM_DATA_PALETTE;

    static {
        List<Entry> entries;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            entries = GSON.fromJson(reader, ENTRY_TYPE);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load runtime_item_ids.json", e);
        }

        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(entries.size());

        LEGACY_NETWORK_MAP.defaultReturnValue(-1);
        NETWORK_LEGACY_MAP.defaultReturnValue(-1);

        for (Entry entry : entries) {
            paletteBuffer.putString(entry.name);
            paletteBuffer.putLShort(entry.id);
            paletteBuffer.putBoolean(false); // Component item

            if (entry.oldId != null && entry.oldId <= 0xff) {
                boolean hasData = entry.oldData != null;
                int fullId = getFullId(entry.oldId, hasData ? entry.oldData : 0);
                LEGACY_NETWORK_MAP.put(fullId, (entry.id << 1) | (hasData ? 1 : 0));
                NETWORK_LEGACY_MAP.put(entry.id, fullId | (hasData ? 1 : 0));
            }
        }

        ITEM_DATA_PALETTE = paletteBuffer.getBuffer();
    }

    @Override
    public int getNetworkFullId0(Item item) {
        int fullId = getFullId(item.getId(), item.hasMeta() ? item.getDamage() : -1);
        int networkId = LEGACY_NETWORK_MAP.get(fullId);
        if (networkId == -1) {
            networkId = LEGACY_NETWORK_MAP.get(getFullId(item.getId(), 0));
        }
        if (networkId == -1) {
            throw new IllegalArgumentException("Unknown item mapping " + item.getId() + ":" + item.getDamage());
        }

        return networkId;
    }

    @Override
    public int getLegacyFullId0(int networkId) {
        int fullId = NETWORK_LEGACY_MAP.get(networkId);
        if (fullId == -1) {
            throw new IllegalArgumentException("Unknown network mapping: " + networkId);
        }
        return fullId;
    }

    @Override
    public int getId0(int fullId) {
        return (short) (fullId >> 16);
    }

    @Override
    public int getData0(int fullId) {
        return ((fullId >> 1) & 0x7fff);
    }

    private static int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    @Override
    public int getNetworkId0(int networkFullId) {
        return networkFullId >> 1;
    }

    @Override
    public boolean hasData0(int id) {
        return (id & 0x1) != 0;
    }

    public static int getNetworkFullId(Item item) {
        return instance.getNetworkFullId0(item);
    }

    public static int getLegacyFullId(int networkId) {
        return instance.getLegacyFullId0(networkId);
    }

    public static int getId(int fullId) {
        return instance.getId0(fullId);
    }

    public static int getData(int fullId) {
        return instance.getData0(fullId);
    }

    public static int getNetworkId(int networkFullId) {
        return instance.getNetworkId0(networkFullId);
    }

    public static boolean hasData(int id) {
        return instance.hasData0(id);
    }
}
