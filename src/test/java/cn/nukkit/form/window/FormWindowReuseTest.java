package cn.nukkit.form.window;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FormWindowReuseTest {
    @Test
    void closingReopenedSimpleFormDoesNotRepeatThePreviousButton() {
        FormWindowSimple window = new FormWindowSimple("title", "content");
        window.addButton(new ElementButton("open child"));
        assertReuse(window, 0);
    }

    @Test
    void closingReopenedModalDoesNotRepeatConfirmation() {
        assertReuse(new FormWindowModal("title", "content", "yes", "no"), true);
    }

    @Test
    void closingReopenedCustomFormDoesNotResubmitOldValues() {
        FormWindowCustom window = new FormWindowCustom("title");
        window.addElement(new ElementInput("name"));
        assertReuse(window, List.of("first"));
    }

    private void assertReuse(FormWindow window, Object response) {
        for (int cycle = 0; cycle < 3; cycle++) {
            assertTrue(window.setResponse(response, 0));
            assertNotNull(window.getResponse());
            assertFalse(window.wasClosed());
            assertTrue(window.setResponse(null, 0));
            assertNull(window.getResponse());
            assertTrue(window.wasClosed());
            assertTrue(window.setResponse(null, 0));
            assertNull(window.getResponse());
        }
    }
}
