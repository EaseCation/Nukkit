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

        public String name;
        public int id;
        public Integer oldId;
        public Integer oldData;

        public Entry(String name, int id, Integer oldId, Integer oldData) {
            this.name = name;
            this.id = id;
            this.oldId = oldId;
            this.oldData = oldData;
        }
    }
}
