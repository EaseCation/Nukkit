package cn.nukkit.resourcepacks;

import javax.annotation.Nullable;
import java.util.List;

public interface ResourcePack {
    String getPackName();

    String getPackId();

    String getPackVersion();

    String getPackType();

    List<String> getCapabilities();

    int getPackSize();

    byte[] getSha256();

    int getChunkCount();

    byte[] getPackChunk(int index);

    String getEncryptionKey();

    @Nullable
    String getCdnUrl();
}
