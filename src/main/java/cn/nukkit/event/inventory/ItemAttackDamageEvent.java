package cn.nukkit.event.inventory;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class ItemAttackDamageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item item;
    private float attackDamage;

	public ItemAttackDamageEvent(Item item) {
		this(item, 1 + item.getAttackDamage());
	}

	public ItemAttackDamageEvent(Item item, float attackDamage) {
		this.item = item;
		this.attackDamage = attackDamage;
	}

	public Item getItem() {
		return item;
	}

	public float getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(float attackDamage) {
		this.attackDamage = attackDamage;
	}
}
