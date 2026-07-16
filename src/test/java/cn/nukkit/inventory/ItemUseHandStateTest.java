package cn.nukkit.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemUseHandStateTest {

    @Test
    void defaultsToMainHand() {
        final ItemUseHandState state = new ItemUseHandState();

        assertEquals(ItemUseHand.MAIN_HAND, state.interactionHand());
        assertEquals(ItemUseHand.MAIN_HAND, state.usingHand());
    }

    @Test
    void capturesAndClearsOffhandUse() {
        final ItemUseHandState state = new ItemUseHandState();
        final ItemUseHand previous = state.setInteractionHand(ItemUseHand.OFF_HAND);

        state.setUsing(true);
        state.setInteractionHand(previous);

        assertEquals(ItemUseHand.MAIN_HAND, state.interactionHand());
        assertEquals(ItemUseHand.OFF_HAND, state.usingHand());

        state.setUsing(false);
        assertEquals(ItemUseHand.MAIN_HAND, state.usingHand());
    }

    @Test
    void nestedInteractionRestoresPreviousHand() {
        final ItemUseHandState state = new ItemUseHandState();
        state.setInteractionHand(ItemUseHand.OFF_HAND);
        final ItemUseHand previous = state.setInteractionHand(ItemUseHand.MAIN_HAND);

        state.setInteractionHand(previous);

        assertEquals(ItemUseHand.OFF_HAND, state.interactionHand());
    }

    @Test
    void reportsOffhandUseOnlyWhileCaptured() {
        final ItemUseHandState state = new ItemUseHandState();

        assertFalse(state.isUsingOffhand());

        state.setInteractionHand(ItemUseHand.OFF_HAND);
        state.setUsing(true);
        assertTrue(state.isUsingOffhand());

        state.setUsing(false);
        assertFalse(state.isUsingOffhand());
    }

    @Test
    void recapturesAnActiveUseForTheCurrentInteractionHand() {
        final ItemUseHandState state = new ItemUseHandState();
        state.setUsing(true);
        state.setInteractionHand(ItemUseHand.OFF_HAND);

        state.setUsing(true);

        assertEquals(ItemUseHand.OFF_HAND, state.usingHand());
        state.setUsing(false);
        assertEquals(ItemUseHand.MAIN_HAND, state.usingHand());
    }

}
