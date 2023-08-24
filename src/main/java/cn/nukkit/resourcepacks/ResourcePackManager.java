package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Log4j2
public class ResourcePackManager {
    private boolean inited;
    private ResourcePack[] resourcePacks;
    private ResourcePack[] behaviorPacks;
    private final Map<String, ResourcePack> allPacksById = new Object2ObjectLinkedOpenHashMap<>();
    private final Map<String, ResourcePack> resourcePacksById = new Object2ObjectLinkedOpenHashMap<>();
    private final Map<String, ResourcePack> behaviorPacksById = new Object2ObjectLinkedOpenHashMap<>();

    public ResourcePackManager(File path) {
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translate("nukkit.resources.invalid-path", path.getName()));
        }

        this.inited = false;
        for (File pack : path.listFiles()) {
            this.tryLoad(pack);
        }
        this.inited = true;

        this.resourcePacks = resourcePacksById.values().toArray(new ResourcePack[0]);
        this.behaviorPacks = behaviorPacksById.values().toArray(new ResourcePack[0]);
        log.info(Server.getInstance().getLanguage()
                .translate("nukkit.resources.success", this.allPacksById.size()));
    }

    public void tryLoad(File pack) {
        try {
            ResourcePack resourcePack = null;

            if (!pack.isDirectory()) { //directory resource packs temporarily unsupported
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
