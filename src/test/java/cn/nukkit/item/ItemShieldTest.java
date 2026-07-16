package cn.nukkit.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ItemShieldTest {

    @Test
    void keepsDefaultBedrockUseHooksDisabled() {
        final ItemShield shield = new ItemShield();

        assertFalse(shield.onClickAir(null, null));
        assertFalse(shield.onRelease(null, 0, null));
        assertFalse(shield.canRelease());
    }
}
