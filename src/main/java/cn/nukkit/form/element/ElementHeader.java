package cn.nukkit.form.element;

import lombok.ToString;

/**
 * @since 1.21.70
 */
@ToString
public class ElementHeader extends Element implements SimpleElement {

    @SuppressWarnings("unused")
    private final String type = "header";
    private String text = "";

    public ElementHeader(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
