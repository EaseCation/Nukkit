package cn.nukkit.resourcepacks;

import java.util.List;

final class ResourceSubPack implements ResourcePack {
    private final ResourcePack pack;
    private final String subPackName;

    private ResourceSubPack(ResourcePack pack, String subPackName) {
        this.pack = pack;
        this.subPackName = subPackName;
    }

    static ResourcePack select(ResourcePack pack, String folderName) {
        if (folderName == null) {
            throw new IllegalArgumentException("Resource pack subpack name must not be null");
        }
        ResourcePack source = pack instanceof ResourceSubPack selected ? selected.pack : pack;
        if (folderName.isEmpty()) {
            if (source.getSubPackName().isEmpty()) {
                return source;
            }
            return pack.getSubPackName().isEmpty() ? pack : new ResourceSubPack(source, "");
        }
        boolean declared = source.getSubPacks().stream().anyMatch(subPack -> folderName.equals(subPack.folderName()));
        if (!declared) {
            throw new IllegalArgumentException("Unknown resource pack subpack: " + folderName);
        }
        if (folderName.equals(pack.getSubPackName())) {
            return pack;
        }
        return new ResourceSubPack(source, folderName);
    }

    @Override
    public String getPackName() {
        return pack.getPackName();
    }

    @Override
    public String getPackId() {
        return pack.getPackId();
    }

    @Override
    public String getPackVersion() {
        return pack.getPackVersion();
    }

    @Override
    public String getPackType() {
        return pack.getPackType();
    }

    @Override
    public boolean isBehaviorPack() {
        return pack.isBehaviorPack();
    }

    @Override
    public List<String> getCapabilities() {
        return pack.getCapabilities();
    }

    @Override
    public List<PackManifest.SubPack> getSubPacks() {
        return pack.getSubPacks();
    }

    @Override
    public String getSubPackName() {
        return subPackName;
    }

    @Override
    public int getPackSize() {
        return pack.getPackSize();
    }

    @Override
    public byte[] getSha256() {
        return pack.getSha256();
    }

    @Override
    public int getChunkCount() {
        return pack.getChunkCount();
    }

    @Override
    public byte[] getPackChunk(int index) {
        return pack.getPackChunk(index);
    }

    @Override
    public String getEncryptionKey() {
        return pack.getEncryptionKey();
    }

    @Override
    public void setEncryptionKey(String key) {
        pack.setEncryptionKey(key);
    }

    @Override
    public String getCdnUrl() {
        return pack.getCdnUrl();
    }

    @Override
    public void setCdnUrl(String url) {
        pack.setCdnUrl(url);
    }
}
