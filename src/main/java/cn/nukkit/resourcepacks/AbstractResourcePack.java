package cn.nukkit.resourcepacks;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public abstract class AbstractResourcePack implements ResourcePack {
    protected PackManifest manifest;

    protected String id;
    protected String version;
    protected String type;
    protected final List<String> capabilities = new ArrayList<>();

    protected String encryptionKey = "";
    protected String cdnUrl = "";

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
    public List<String> getCapabilities() {
        return capabilities;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

    @Override
    public void setEncryptionKey(String key) {
        this.encryptionKey = key;
    }

    @Override
    public String getCdnUrl() {
        return cdnUrl;
    }

    @Override
    public void setCdnUrl(String url) {
        this.cdnUrl = url;
    }
}
