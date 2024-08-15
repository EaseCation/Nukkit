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

    public Map<String, ResourcePack> getBehaviourPacks() {
        return behaviourPacks;
    }

    public void putBehaviourPack(ResourcePack... pack) {
        for (ResourcePack p : pack) {
            this.behaviourPacks.put(p.getPackId(), p);
        }
    }

    public boolean isMustAccept() {
        return mustAccept;
    }

    public PlayerRequestResourcePackEvent setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
        return this;
    }
}
