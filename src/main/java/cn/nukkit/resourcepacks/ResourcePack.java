package cn.nukkit.resourcepacks;

public interface ResourcePack {
    String getPackName();

    String getPackId();

    String getPackVersion();

    String getPackType();

    int getPackSize();

    byte[] getSha256();

    int getChunkCount();

    byte[] getPackChunk(int index);

    String getEncryptionKey();
}
