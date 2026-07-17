package cn.nukkit.item.food;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodContainerDeliveryPolicyTest {

    @Test
    void deliversContainersDuringOrdinarySurvivalEating() {
        assertTrue(FoodContainerDeliveryPolicy.shouldDeliverDuringEating(true, false));
    }

    @Test
    void leavesOffhandContainerDeliveryToItemEdible() {
        assertFalse(FoodContainerDeliveryPolicy.shouldDeliverDuringEating(true, true));
    }

    @Test
    void doesNotDeliverContainersInCreativeLikeModes() {
        assertFalse(FoodContainerDeliveryPolicy.shouldDeliverDuringEating(false, false));
        assertFalse(FoodContainerDeliveryPolicy.shouldDeliverDuringEating(false, true));
    }
}
