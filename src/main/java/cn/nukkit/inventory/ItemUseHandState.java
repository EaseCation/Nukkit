package cn.nukkit.inventory;

import java.util.Objects;

public final class ItemUseHandState {

    private ItemUseHand interactionHand = ItemUseHand.MAIN_HAND;
    private ItemUseHand usingHand = ItemUseHand.MAIN_HAND;

    public ItemUseHand interactionHand() {
        return this.interactionHand;
    }

    public ItemUseHand setInteractionHand(ItemUseHand hand) {
        ItemUseHand previous = this.interactionHand;
        this.interactionHand = Objects.requireNonNull(hand, "hand");
        return previous;
    }

    public ItemUseHand usingHand() {
        return this.usingHand;
    }

    public boolean isUsingOffhand() {
        return this.usingHand == ItemUseHand.OFF_HAND;
    }

    public void setUsing(boolean using) {
        this.usingHand = using ? this.interactionHand : ItemUseHand.MAIN_HAND;
    }

}
