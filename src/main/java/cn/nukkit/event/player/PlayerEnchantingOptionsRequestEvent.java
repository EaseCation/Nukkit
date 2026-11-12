package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.enchantment.EnchantmentOption;

import java.util.ArrayList;
import java.util.List;

public class PlayerEnchantingOptionsRequestEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final EnchantInventory inventory;
    private List<EnchantmentOption> options;

    public PlayerEnchantingOptionsRequestEvent(Player player, EnchantInventory inventory, List<EnchantmentOption> options) {
        this.player = player;
        this.inventory = inventory;
        this.options = new ArrayList<>(options);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EnchantInventory getInventory() {
        return inventory;
    }

    public List<EnchantmentOption> getOptions() {
        return List.copyOf(options);
    }

    public void setOptions(List<EnchantmentOption> options) {
        if (options.size() > 3 || options.stream().anyMatch(option -> option == null)) {
            throw new IllegalArgumentException("An enchanting table requires at most three non-null options");
        }
        this.options = new ArrayList<>(options);
    }
}
