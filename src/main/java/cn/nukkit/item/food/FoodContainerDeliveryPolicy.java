package cn.nukkit.item.food;

final class FoodContainerDeliveryPolicy {

    private FoodContainerDeliveryPolicy() {
    }

    static boolean shouldDeliverDuringEating(boolean survivalLike, boolean offhandInteraction) {
        return survivalLike && !offhandInteraction;
    }
}
