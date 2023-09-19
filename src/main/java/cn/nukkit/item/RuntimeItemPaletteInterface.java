package cn.nukkit.item;

import lombok.ToString;

public interface RuntimeItemPaletteInterface {

    int getNetworkFullId0(Item item);

    int getLegacyFullId0(int networkId);

    int getId0(int fullId);

    int getData0(int fullId);

    int getNetworkId0(int networkFullId);

    boolean hasData0(int id);

    @ToString
    class Entry {

        public final String name;
        public final int id;
        public final Integer oldId;
        public final Integer oldData;
        public final boolean component;

        public Entry(String name, int id, Integer oldId, Integer oldData, boolean component) {
            this.name = name;
            this.id = id;
            this.oldId = oldId;
            this.oldData = oldData;
            this.component = component;
        }
    }
}
