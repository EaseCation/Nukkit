package cn.nukkit.form.element;

import lombok.ToString;

@ToString
public class ElementLabel implements Element, SimpleElement {

    @SuppressWarnings("unused")
    private final String type = "label"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";

    public ElementLabel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
