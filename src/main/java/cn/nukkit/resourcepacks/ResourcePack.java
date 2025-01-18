package cn.nukkit.resourcepacks;

import java.util.List;

public interface ResourcePack {
    String getPackName();

    String getPackId();

    String getPackVersion();

    String getPackType();

    List<String> getCapabilities();

    default boolean isBehaviorPack() {
        return "data".equals(getPackType());
    }

    int getPackSize();

    byte[] getSha256();

    int getChunkCount();

    byte[] getPackChunk(int index);

    String getEncryptionKey();

    void setEncryptionKey(String key);

    String getCdnUrl();

    void setCdnUrl(String url);
}
