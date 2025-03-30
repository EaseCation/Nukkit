package cn.nukkit.form.element;

import lombok.ToString;

/**
 * @since 1.21.70
 */
@ToString
public class ElementDivider extends Element implements SimpleElement {

    @SuppressWarnings("unused")
    private final String type = "divider";
    private String text = "";

    public ElementDivider() {
        this("");
    }

    public ElementDivider(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
