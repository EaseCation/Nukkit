package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
public class ResourcePackManager {
    private final File path;
    private boolean inited;
    private ResourcePack[] resourcePacks;
    private ResourcePack[] behaviorPacks;
    private final Map<String, ResourcePack> allPacksById = new Object2ObjectLinkedOpenHashMap<>();
    private final Map<String, ResourcePack> resourcePacksById = new Object2ObjectLinkedOpenHashMap<>();
    private final Map<String, ResourcePack> behaviorPacksById = new Object2ObjectLinkedOpenHashMap<>();

    public ResourcePackManager(File path) {
        this.path = path;
        reload(path);
    }

    public void reload() {
        reload(path);
    }

    public void reload(File path) {
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translate("nukkit.resources.invalid-path", path.getName()));
        }

        this.inited = false;
        this.allPacksById.clear();
        this.resourcePacksById.clear();
        this.behaviorPacksById.clear();

        for (File pack : path.listFiles()) {
            this.tryLoad(pack);
        }
        this.inited = true;

        Map<String, ResourcePack> behaviorStack = new Object2ObjectLinkedOpenHashMap<>();
        Map<String, ResourcePack> resourceStack = new Object2ObjectLinkedOpenHashMap<>();
        Map<String, ResourcePack> allStack = new Object2ObjectLinkedOpenHashMap<>();

        File configFile = new File(path, "packs.yml");
        if (!configFile.exists()) {
            try {
                Utils.writeFile(configFile, Server.class.getClassLoader().getResourceAsStream("packs.yml"));
            } catch (IOException e) {
                log.warn("Failed to load pack config", e);
            }
        }

        Config config = new Config(configFile);
        for (String packId : config.getSections().keySet()) {
            ResourcePack pack = getPackById(packId);
            if (pack == null) {
                continue;
            }

            String key = config.getString(packId + ".key");
            if (!key.isEmpty()) {
                pack.setEncryptionKey(key);
            }

            String cdn = config.getString(packId + ".cdn");
            if (!cdn.isEmpty()) {
                pack.setCdnUrl(cdn);
            }

            if (pack.isBehaviorPack()) {
                behaviorStack.put(packId, pack);
            } else {
                resourceStack.put(packId, pack);
            }
            allStack.put(packId, pack);
        }

        for (Entry<String, ResourcePack> entry : this.behaviorPacksById.entrySet()) {
            String id = entry.getKey();
            ResourcePack pack = entry.getValue();
            behaviorStack.putIfAbsent(id, pack);
            allStack.putIfAbsent(id, pack);
        }
        for (Entry<String, ResourcePack> entry : this.resourcePacksById.entrySet()) {
            String id = entry.getKey();
            ResourcePack pack = entry.getValue();
            resourceStack.putIfAbsent(id, pack);
            allStack.putIfAbsent(id, pack);
        }

        this.behaviorPacksById.clear();
        this.resourcePacksById.clear();
        this.allPacksById.clear();

        this.behaviorPacksById.putAll(behaviorStack);
        this.resourcePacksById.putAll(resourceStack);
        this.allPacksById.putAll(allStack);

        this.resourcePacks = resourcePacksById.values().toArray(new ResourcePack[0]);
        this.behaviorPacks = behaviorPacksById.values().toArray(new ResourcePack[0]);
        log.info(Server.getInstance().getLanguage()
                .translate("nukkit.resources.success", this.allPacksById.size()));
    }

    public void tryLoad(File pack) {
        try {
            ResourcePack resourcePack = null;

            if (!pack.isDirectory()) {
                switch (Files.getFileExtension(pack.getName())) {
                    case "zip":
                    case "mcpack":
                        resourcePack = new ZippedResourcePack(pack);
                        break;
                    default:
                        log.warn(Server.getInstance().getLanguage()
                            .translate("nukkit.resources.unknown-format", pack.getName()));
                        break;
                }
            } else {
                File tempPack = loadDirectoryPack(pack);
                if (tempPack == null) {
                    return;
                }
                resourcePack = new ZippedResourcePack(tempPack);
            }

            if (resourcePack != null) {
                if (resourcePack.getPackType().equals("resources")) {
                    this.resourcePacksById.put(resourcePack.getPackId(), resourcePack);
                    this.allPacksById.put(resourcePack.getPackId(), resourcePack);
                } else if (resourcePack.getPackType().equals("data")) {
                    this.behaviorPacksById.put(resourcePack.getPackId(), resourcePack);
                    this.allPacksById.put(resourcePack.getPackId(), resourcePack);
                } else {
                    log.warn(Server.getInstance().getLanguage()
                        .translate("nukkit.resources.unknown-format", pack.getName()));
                }
            } else {
                return;
            }
        } catch (IllegalArgumentException | IOException e) {
            log.warn(Server.getInstance().getLanguage()
                .translate("nukkit.resources.fail", pack.getName()), e);
        }
        if (inited) {
            this.resourcePacks = resourcePacksById.values().toArray(new ResourcePack[0]);
            this.behaviorPacks = behaviorPacksById.values().toArray(new ResourcePack[0]);
        }
    }

    @Nullable
    private static File loadDirectoryPack(File directory) {
        File manifestFile = new File(directory, "manifest.json");
        if (!manifestFile.exists() || !manifestFile.isFile()) {
            return null;
        }

        File tempFile;
        try {
            tempFile = File.createTempFile("pack", ".zip");
            tempFile.deleteOnExit();

            FileTime time = FileTime.fromMillis(0);
            try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(tempFile))) {
                stream.setLevel(Deflater.BEST_COMPRESSION);

                Collection<File> files = new TreeSet<>(FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
                for (File file : files) {
                    ZipEntry entry = new ZipEntry(directory.toPath().relativize(file.toPath()).toString())
                            .setCreationTime(time)
                            .setLastModifiedTime(time)
                            .setLastAccessTime(time);
                    stream.putNextEntry(entry);
                    stream.write(Files.toByteArray(file));
                    stream.closeEntry();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temporary mcpack file", e);
        }
        return tempFile;
    }

    public Map<String, ResourcePack> getResourcePacksMap() {
        return resourcePacksById;
    }

    public Map<String, ResourcePack> getBehaviorPacksMap() {
        return behaviorPacksById;
    }

    public ResourcePack[] getResourceStack() {
        return this.resourcePacks;
    }

    public ResourcePack[] getBehaviorStack() {
        return this.behaviorPacks;
    }

    public ResourcePack getPackById(String id) {
        return this.allPacksById.get(id);
    }
}
