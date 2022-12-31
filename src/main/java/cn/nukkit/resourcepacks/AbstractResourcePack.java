package cn.nukkit.resourcepacks;

import lombok.ToString;

@ToString
public abstract class AbstractResourcePack implements ResourcePack {
    protected PackManifest manifest;

    protected String id;
    protected String version;
    protected String type;

    @Override
    public String getPackName() {
        return this.manifest.getHeader().getName();
    }

    @Override
    public String getPackId() {
        return id;
    }

    @Override
    public String getPackVersion() {
        return version;
    }

    @Override
    public String getPackType() {
        return type;
    }

    @Override
    public String getEncryptionKey() {
        return "";
    }
}
