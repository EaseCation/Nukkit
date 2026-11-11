package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.resourcepacks.ResourcePack;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerRequestResourcePackEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Map<String, ResourcePack> resourcePacks = new LinkedHashMap<>();
    private final Map<String, ResourcePack> behaviourPacks = new LinkedHashMap<>();
    private boolean mustAccept;

    public PlayerRequestResourcePackEvent(Player player, Map<String, ResourcePack> resourcePacks, Map<String, ResourcePack> behaviourPacks, boolean mustAccept) {
        this.player = player;
        this.resourcePacks.putAll(resourcePacks);
        this.behaviourPacks.putAll(behaviourPacks);
        this.mustAccept = mustAccept;
    }

    public Map<String, ResourcePack> getResourcePacks() {
        return resourcePacks;
    }

    public void putResourcePack(ResourcePack... pack) {
        for (ResourcePack p : pack) {
            this.resourcePacks.put(p.getPackId(), p);
        }
    }

    /**
     * Select the resource subpack for this login; an empty name preserves the client's default selection behavior.
     */
    public void setResourcePackSubPack(String packId, String folderName) {
        selectSubPack(resourcePacks, packId, folderName);
    }

    private static void selectSubPack(Map<String, ResourcePack> packs, String packId, String folderName) {
        ResourcePack pack = packs.get(packId);
        if (pack == null) {
            throw new IllegalArgumentException("Resource pack is not offered: " + packId);
        }
        packs.replace(packId, pack.withSubPack(folderName));
    }

    public Map<String, ResourcePack> getBehaviourPacks() {
        return behaviourPacks;
    }

    public void putBehaviourPack(ResourcePack... pack) {
        for (ResourcePack p : pack) {
            this.behaviourPacks.put(p.getPackId(), p);
        }
    }

    /**
     * Select the behavior subpack for this login; an empty name preserves the client's default selection behavior.
     */
    public void setBehaviorPackSubPack(String packId, String folderName) {
        selectSubPack(behaviourPacks, packId, folderName);
    }

    public boolean isMustAccept() {
        return mustAccept;
    }

    public PlayerRequestResourcePackEvent setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
        return this;
    }
}
