package cn.nukkit.item;

import lombok.ToString;

import javax.annotation.Nullable;

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
        @Nullable
        public final Integer oldId;
        @Nullable
        public final Integer oldData;
        public final boolean component;

        public Entry(String name, int id, @Nullable Integer oldId, @Nullable Integer oldData, boolean component) {
            this.name = name;
            this.id = id;
            this.oldId = oldId;
            this.oldData = oldData;
            this.component = component;
        }
    }
}
