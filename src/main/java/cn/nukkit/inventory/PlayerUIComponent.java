package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public abstract class PlayerUIComponent extends BaseInventory {

    protected final PlayerUIInventory playerUI;
    protected final int offset;

    PlayerUIComponent(PlayerUIInventory playerUI, int offset, int size) {
        super(playerUI.holder, InventoryType.UI, Collections.emptyMap(), size);
        this.playerUI = playerUI;
        this.offset = offset;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setMaxStackSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item getItem(int index) {
        if (isServerAuthoritativeInventoryEnabled()) {
            return super.getItem(index);
        }
        return this.playerUI.getItem(realIndex(index));
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (isServerAuthoritativeInventoryEnabled()) {
            return super.setItem(index, item, send);
        }
        return this.playerUI.setItem(realIndex(index), item, send);
    }

    @Override
    public boolean clear(int index, boolean send) {
        if (isServerAuthoritativeInventoryEnabled()) {
            return super.clear(index, send);
        }
        if (playerUI == null) {
            return false;
        }
        return playerUI.clear(realIndex(index), send);
    }

    @Override
    public Int2ObjectMap<Item> getContents() {
        if (isServerAuthoritativeInventoryEnabled()) {
            return super.getContents();
        }
        Int2ObjectMap<Item> contents = new Int2ObjectOpenHashMap<>(10);
        for (Int2ObjectMap.Entry<Item> entry : playerUI.slots.int2ObjectEntrySet()) {
            int slot = entry.getIntKey();
            if (slot < offset || slot >= offset + size) {
                continue;
            }
            contents.put(slot, entry.getValue());
        }
        return contents;
    }

    @Override
    public void sendContents(Player... players) {
        if (isServerAuthoritativeInventoryEnabled()) {
            super.sendContents(players);
            return;
        }
        this.playerUI.sendContents(players);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        if (isServerAuthoritativeInventoryEnabled()) {
            super.sendSlot(index, players);
            return;
        }
        playerUI.sendSlot(realIndex(index), players);
    }

    @Override
    public Set<Player> getViewers() {
        if (isServerAuthoritativeInventoryEnabled()) {
            return super.getViewers();
        }
        return playerUI.viewers;
    }

    @Override
    public InventoryType getType() {
        return playerUI.type;
    }

    @Override
    public void onOpen(Player who) {
        if (isServerAuthoritativeInventoryEnabled()) {
            super.onOpen(who);
        }

    }

    @Override
    public boolean open(Player who) {
        if (isServerAuthoritativeInventoryEnabled()) {
            return super.open(who);
        }
        return false;
    }

    @Override
    public void close(Player who) {
        if (isServerAuthoritativeInventoryEnabled()) {
            super.close(who);
        }

    }

    @Override
    public void onClose(Player who) {
        if (isServerAuthoritativeInventoryEnabled()) {
            super.onClose(who);
        }

    }

    @Override
    public void onSlotChange(int index, Item before, Item after, boolean send) {
        if (isServerAuthoritativeInventoryEnabled()) {
            super.onSlotChange(index, before, after, send);
            return;
        }
        this.playerUI.onSlotChange(realIndex(index), before, after, send);
    }

    protected boolean isServerAuthoritativeInventoryEnabled() {
        return playerUI != null && playerUI.getHolder().isServerAuthoritativeInventoryEnabled();
    }

    @Override
    @Nullable
    protected Entity getInventoryChangeEntity() {
        return isServerAuthoritativeInventoryEnabled() ? playerUI.getHolder() : super.getInventoryChangeEntity();
    }

    @Override
    protected int getInventoryChangeSlot(int index) {
        return isServerAuthoritativeInventoryEnabled() ? realIndex(index) : super.getInventoryChangeSlot(index);
    }

    protected int realIndex(int index) {
        return index + this.offset;
    }

    public boolean canTakeResult(Player player) {
        return true;
    }

    public boolean preTakeResult(Player player) {
        return true;
    }

    public boolean onTakeResult(Player player, Item result) {
        return true;
    }

    public void postTakeResultResolve(Player player) {
    }

    public void postTakeResultReject(Player player) {
        sendContents(player);
    }

    @Deprecated
    @Nullable
    public RecipeTag getRecipeTag() {
        return null;
    }
}
