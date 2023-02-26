package cn.nukkit.event.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package cn.nukkit.event.potion in project nukkit
 */
public class PotionApplyEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item potionItem;
    private Effect[] applyEffects;

    private final Entity entity;

    public PotionApplyEvent(Potion potion, Item potionItem, Effect[] applyEffects, Entity entity) {
        super(potion);
        this.potionItem = potionItem;
        this.applyEffects = applyEffects;
        this.entity = entity;
    }

    public Item getPotionItem() {
        return potionItem;
    }

    public Entity getEntity() {
        return entity;
    }

    public Effect[] getApplyEffects() {
        return applyEffects;
    }

    public void setApplyEffect(Effect... applyEffects) {
        this.applyEffects = applyEffects;
    }
}
